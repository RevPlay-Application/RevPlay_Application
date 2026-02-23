
const Player = {
    audio: new Audio(),
    isPlaying: false,
    isShuffle: false,
    repeatMode: 0, // 0: none, 1: all, 2: one
    queue: [],
    currentIndex: -1,
    originalQueue: [], // To restore after shuffle

    init() {
        this.audio.addEventListener('timeupdate', () => this.updateProgress());
        this.audio.addEventListener('ended', () => this.handleEnded());
        this.audio.addEventListener('loadedmetadata', () => this.updateDuration());

        // Bind UI controls
        const playPauseBtn = document.getElementById('playPauseBtn');
        if (playPauseBtn) playPauseBtn.addEventListener('click', () => this.togglePlay());

        const prevBtn = document.getElementById('prevBtn');
        if (prevBtn) prevBtn.addEventListener('click', () => this.prev());

        const nextBtn = document.getElementById('nextBtn');
        if (nextBtn) nextBtn.addEventListener('click', () => this.next());

        const shuffleBtn = document.getElementById('shuffleBtn');
        if (shuffleBtn) shuffleBtn.addEventListener('click', () => this.toggleShuffle());

        const repeatBtn = document.getElementById('repeatBtn');
        if (repeatBtn) repeatBtn.addEventListener('click', () => this.toggleRepeat());

        const progressBarWrapper = document.getElementById('progressBarWrapper');
        if (progressBarWrapper) progressBarWrapper.addEventListener('click', (e) => this.seek(e));

        // Volume Controls
        const volumeSlider = document.getElementById('volumeSlider');
        if (volumeSlider) {
            volumeSlider.addEventListener('input', (e) => this.setVolume(e.target.value));
            // Set initial volume
            this.audio.volume = volumeSlider.value;
        }

        const favBtn = document.getElementById('favBtn');
        if (favBtn) favBtn.addEventListener('click', () => this.toggleFavorite());

        const playlistBtn = document.getElementById('playlistBtn');
        if (playlistBtn) playlistBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            this.togglePlaylistModal();
        });

        // Close modal when clicking outside
        document.addEventListener('click', (e) => {
            const modal = document.getElementById('addToPlaylistModal');
            if (modal && modal.style.display === 'block' && !modal.contains(e.target)) {
                modal.style.display = 'none';
            }
        });

        // Load last played song
        this.loadLastPlayed();
    },

    togglePlaylistModal() {
        const modal = document.getElementById('addToPlaylistModal');
        if (!modal) return;

        if (modal.style.display === 'none' || modal.style.display === '') {
            modal.style.display = 'block';
            this.loadUserPlaylists();
        } else {
            modal.style.display = 'none';
        }
    },

    async loadUserPlaylists() {
        const listEl = document.getElementById('playlistList');
        if (!listEl) return;

        try {
            const response = await fetch('/user/playlists/json');
            const playlists = await response.json();

            if (playlists.length === 0) {
                listEl.innerHTML = '<p style="font-size: 0.8rem; color: var(--text-secondary); padding: 0.5rem;">No playlists found. Create one first!</p>';
                return;
            }

            listEl.innerHTML = playlists.map(p => `
                <div onclick="Player.addSongToPlaylist(${p.id})" 
                     style="padding: 0.5rem; cursor: pointer; border-radius: 4px; transition: background 0.2s; font-size: 0.9rem;"
                     onmouseover="this.style.background='rgba(255,255,255,0.1)'" 
                     onmouseout="this.style.background='transparent'">
                    📁 ${p.name}
                </div>
            `).join('');
        } catch (err) {
            listEl.innerHTML = '<p style="color: #ff4d4d; font-size: 0.8rem;">Failed to load playlists</p>';
        }
    },

    async addSongToPlaylist(playlistId) {
        const song = this.queue[this.currentIndex];
        if (!song) return;

        try {
            const formData = new URLSearchParams();
            formData.append('playlistId', playlistId);
            formData.append('songId', song.id);

            const response = await fetch('/user/playlists/add', {
                method: 'POST',
                body: formData,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
            const data = await response.json();

            if (data.success) {
                App.showToast('Added to playlist!', 'success');
                document.getElementById('addToPlaylistModal').style.display = 'none';
            } else {
                App.showToast('Error: ' + (data.message || 'Failed to add song'), 'error');
            }
        } catch (err) {
            console.error('Failed to add song to playlist', err);
            App.showToast('Something went wrong.', 'error');
        }
    },

    setVolume(value) {
        this.audio.volume = value;
    },

    playSong(songId, contextSongs = null) {
        if (contextSongs && contextSongs.length > 0) {
            this.originalQueue = [...contextSongs];
            this.queue = this.isShuffle ? this.shuffleArray([...this.originalQueue]) : [...this.originalQueue];
            this.currentIndex = this.queue.findIndex(s => s.id === songId);
        } else {
            // Find songId in existing queue if no context
            const index = this.queue.findIndex(s => s.id === songId);
            if (index !== -1) {
                this.currentIndex = index;
            } else if (this.currentIndex === -1) {
                // Last ditch effort: create a solo queue
                this.queue = [{ id: songId }];
                this.currentIndex = 0;
            }
        }

        const song = this.queue[this.currentIndex];
        if (!song) return;

        this.loadSong(song);
        this.audio.play()
            .then(() => {
                this.isPlaying = true;
                this.updatePlayPauseIcon();
                this.updatePlayerUI(song);
                this.updateMediaSession(song);
                const playerEl = document.querySelector('.music-player');
                if (playerEl) playerEl.classList.remove('hidden');
            })
            .catch(err => console.error("Playback failed:", err));

        this.saveLastPlayed(song);
        this.checkFavoriteStatus(song.id);
    },

    loadSong(song) {
        this.audio.src = `/songs/stream/${song.id}`;
    },

    togglePlay() {
        if (!this.audio.src) return;

        if (this.audio.paused) {
            this.audio.play();
            this.isPlaying = true;
        } else {
            this.audio.pause();
            this.isPlaying = false;
        }
        this.updatePlayPauseIcon();
    },

    prev() {
        if (this.queue.length === 0) return;
        if (this.audio.currentTime > 3) {
            this.audio.currentTime = 0;
            return;
        }

        let newIndex = this.currentIndex - 1;
        if (newIndex < 0) {
            if (this.repeatMode === 1) {
                newIndex = this.queue.length - 1;
            } else {
                // Radio mode backwards: play a random song
                this.playRandomSystemSong();
                return;
            }
        }

        this.currentIndex = newIndex;
        this.playSong(this.queue[this.currentIndex].id);
    },

    next() {
        if (this.queue.length === 0) return;

        let newIndex = this.currentIndex + 1;
        if (newIndex >= this.queue.length) {
            if (this.repeatMode === 1) { // Loop all
                newIndex = 0;
            } else {
                // Play random or stop
                this.playRandomSystemSong();
                return;
            }
        }

        this.currentIndex = newIndex;
        this.playSong(this.queue[this.currentIndex].id);
    },

    async playRandomSystemSong() {
        try {
            const response = await fetch('/songs/random');
            const song = await response.json();
            if (song) {
                this.playSong(song.songId, [{
                    id: song.songId,
                    title: song.title,
                    artist: song.artist.artistName,
                    coverId: song.album ? song.album.albumId : null
                }], 0);
                App.showToast('📻 Playing related song...', 'success');
            }
        } catch (err) {
            this.isPlaying = false;
            this.updatePlayPauseIcon();
        }
    },

    handleEnded() {
        if (this.repeatMode === 2) { // Loop one
            this.audio.currentTime = 0;
            this.audio.play();
        } else {
            this.next();
        }
    },

    toggleShuffle() {
        this.isShuffle = !this.isShuffle;
        const btn = document.getElementById('shuffleBtn');
        if (btn) btn.classList.toggle('active', this.isShuffle);

        if (this.queue.length === 0) return;

        const currentSong = this.queue[this.currentIndex];

        if (this.isShuffle) {
            this.queue = this.shuffleArray([...this.originalQueue]);
            // Find new index of current song to keep playing seamless
            this.currentIndex = this.queue.findIndex(s => s.id === currentSong.id);
        } else {
            this.queue = [...this.originalQueue];
            this.currentIndex = this.queue.findIndex(s => s.id === currentSong.id);
        }
    },

    toggleRepeat() {
        this.repeatMode = (this.repeatMode + 1) % 3;
        const btn = document.getElementById('repeatBtn');
        if (!btn) return;

        btn.classList.toggle('active', this.repeatMode > 0);
        if (this.repeatMode === 2) {
            btn.innerHTML = '🔂'; // Repeat One
        } else {
            btn.innerHTML = '🔁'; // Repeat All
        }
    },

    seek(e) {
        if (!this.audio.src) return;
        const wrapper = document.getElementById('progressBarWrapper');
        const width = wrapper.clientWidth;
        const clickX = e.offsetX;
        const duration = this.audio.duration;

        this.audio.currentTime = (clickX / width) * duration;
    },

    updateProgress() {
        const { duration, currentTime } = this.audio;
        if (isNaN(duration)) return;
        const percent = (currentTime / duration) * 100;
        const bar = document.getElementById('progressBar');
        if (bar) bar.style.width = `${percent}%`;

        const timeEl = document.getElementById('currentTime');
        if (timeEl) timeEl.innerText = this.formatTime(currentTime);
    },

    updateDuration() {
        const totalTimeEl = document.getElementById('totalTime');
        if (totalTimeEl) totalTimeEl.innerText = this.formatTime(this.audio.duration);
    },

    updatePlayPauseIcon() {
        const btn = document.getElementById('playPauseBtn');
        if (btn) btn.innerHTML = this.isPlaying ? '⏸' : '▶';
    },

    updatePlayerUI(song) {
        const titleEl = document.getElementById('playerTitle');
        if (titleEl) titleEl.innerText = song.title || 'Unknown Title';

        const artistEl = document.getElementById('playerArtist');
        if (artistEl) artistEl.innerText = song.artist || 'Unknown Artist';

        const coverImg = document.getElementById('playerCover');
        if (coverImg) {
            // Logic suggested by user: use individual if available, else album
            // But we already do this in getSongCover endpoint.
            // We can just rely on /songs/cover/{id} which has the fallback logic.
            coverImg.src = `/songs/cover/${song.id}`;
            coverImg.onerror = function () {
                this.src = '/images/music-placeholder.svg';
            };
        }
    },

    updateMediaSession(song) {
        if ('mediaSession' in navigator) {
            navigator.mediaSession.metadata = new MediaMetadata({
                title: song.title,
                artist: song.artist,
                album: 'RevPlay',
                artwork: [
                    { src: `/songs/cover/${song.id}`, sizes: '512x512', type: 'image/jpeg' }
                ]
            });

            navigator.mediaSession.setActionHandler('play', () => this.togglePlay());
            navigator.mediaSession.setActionHandler('pause', () => this.togglePlay());
            navigator.mediaSession.setActionHandler('previoustrack', () => this.prev());
            navigator.mediaSession.setActionHandler('nexttrack', () => this.next());
        }
    },

    formatTime(seconds) {
        const min = Math.floor(seconds / 60);
        const sec = Math.floor(seconds % 60);
        return `${min}:${sec < 10 ? '0' : ''}${sec}`;
    },

    shuffleArray(array) {
        for (let i = array.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [array[i], array[j]] = [array[j], array[i]];
        }
        return array;
    },

    saveLastPlayed(song) {
        localStorage.setItem('lastPlayedSong', JSON.stringify(song));
    },

    async loadLastPlayed() {
        const lastSong = localStorage.getItem('lastPlayedSong');
        if (lastSong) {
            const song = JSON.parse(lastSong);
            this.queue = [song];
            this.originalQueue = [song];
            this.currentIndex = 0;
            this.updatePlayerUI(song);
            this.checkFavoriteStatus(song.id);
            this.loadSong(song);

            const playerEl = document.querySelector('.music-player');
            if (playerEl) playerEl.classList.remove('hidden');

            // Proactively load more songs for the queue so next/prev work
            try {
                const response = await fetch('/songs/random');
                const randomSong = await response.json();
                if (randomSong && randomSong.songId !== song.id) {
                    const mapped = {
                        id: randomSong.songId,
                        title: randomSong.title,
                        artist: randomSong.artist.artistName,
                        coverId: randomSong.album ? randomSong.album.albumId : null
                    };
                    this.queue.push(mapped);
                    this.originalQueue.push(mapped);
                }
            } catch (e) { console.warn("Failed to pre-fill queue", e); }
        }
    },

    async toggleFavorite() {
        const song = this.queue[this.currentIndex];
        if (!song) return;

        try {
            const response = await fetch(`/user/favorites/toggle/${song.id}`, { method: 'POST' });
            const data = await response.json();
            this.updateFavoriteUI(data.isFavorite);
        } catch (err) {
            console.error('Failed to toggle favorite', err);
        }
    },

    async checkFavoriteStatus(songId) {
        try {
            const response = await fetch(`/user/favorites/check/${songId}`);
            const data = await response.json();
            this.updateFavoriteUI(data.isFavorite);
        } catch (err) {
            console.error('Failed to check favorite status', err);
        }
    },

    updateFavoriteUI(isFavorite) {
        const btn = document.getElementById('favBtn');
        if (btn) {
            btn.innerHTML = isFavorite ? '❤️' : '♡';
            btn.style.color = isFavorite ? '#ff4d4d' : 'var(--text-secondary)';
        }
    }
};

const App = {
    init() {
        // Intercept links
        document.addEventListener('click', (e) => {
            const link = e.target.closest('a');
            if (link && link.href && link.href.startsWith(window.location.origin) && !link.getAttribute('target') && !link.getAttribute('download')) {
                // Ignore logout/login/resources
                if (link.href.includes('/logout') || link.href.includes('/login') || link.href.includes('.')) return;

                e.preventDefault();
                this.navigate(link.href);
            }
        });

        // Handle browser back/forward
        window.addEventListener('popstate', () => {
            this.loadContent(window.location.href);
        });

        // Profile Menu Toggle
        const profileMenu = document.querySelector('.profile-menu');
        if (profileMenu) {
            const circle = profileMenu.querySelector('.profile-circle');
            if (circle) {
                circle.addEventListener('click', (e) => {
                    e.stopPropagation(); // Prevent document click from closing it immediately
                    profileMenu.classList.toggle('active');
                });
            }
            // Close when clicking outside
            document.addEventListener('click', () => {
                profileMenu.classList.remove('active');
            });
        }

        // Intercept Forms
        document.addEventListener('submit', async (e) => {
            const form = e.target;
            if (form.getAttribute('action') && !form.getAttribute('target') && !form.hasAttribute('data-no-spa')) {
                e.preventDefault();
                await this.handleFormSubmit(form);
            }
        });

        // Initial setup
        Player.init();
        this.createToastContainer();
    },

    createToastContainer() {
        if (!document.querySelector('.toast-container')) {
            const container = document.createElement('div');
            container.className = 'toast-container';
            document.body.appendChild(container);
        }
    },

    showToast(message, type = 'success') {
        this.createToastContainer();
        const container = document.querySelector('.toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `<span>${type === 'success' ? '✅' : '❌'}</span> ${message}`;
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    },

    async showConfirm(title, message, onConfirm) {
        let overlay = document.querySelector('.custom-modal-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.className = 'custom-modal-overlay';
            overlay.innerHTML = `
                <div class="custom-modal">
                    <h2 id="modalTitle"></h2>
                    <p id="modalMessage"></p>
                    <div class="modal-actions">
                        <button class="btn" id="modalCancel">Cancel</button>
                        <button class="btn btn-primary" id="modalConfirm">Confirm</button>
                    </div>
                </div>
            `;
            document.body.appendChild(overlay);
        }

        document.getElementById('modalTitle').innerText = title;
        document.getElementById('modalMessage').innerText = message;
        overlay.style.display = 'flex';

        return new Promise((resolve) => {
            const cleanup = () => {
                overlay.style.display = 'none';
            };

            document.getElementById('modalCancel').onclick = () => {
                cleanup();
                resolve(false);
            };
            document.getElementById('modalConfirm').onclick = () => {
                cleanup();
                if (onConfirm) onConfirm();
                resolve(true);
            };
        });
    },

    async handleFormSubmit(form) {
        try {
            const formData = new FormData(form);
            const method = (form.method || 'POST').toUpperCase();
            let url = form.action;

            let response;
            if (method === 'GET') {
                const params = new URLSearchParams(formData);
                url = url.split('?')[0] + '?' + params.toString();
                response = await fetch(url);
            } else {
                response = await fetch(url, {
                    method: method,
                    body: formData
                });
            }

            if (response.redirected) {
                this.navigate(response.url);
            } else {
                // If it's a JSON response (like clear history), process it
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const data = await response.json();
                    if (data.success) {
                        this.navigate(window.location.href); // Refresh current
                        if (data.message) this.showToast(data.message);
                    }
                    return;
                }
                this.navigate(url);
            }
        } catch (err) {
            console.error('Form submission failed', err);
            form.submit();
        }
    },

    async navigate(url) {
        if (url === window.location.href) {
            await this.loadContent(url);
        } else {
            history.pushState(null, '', url);
            await this.loadContent(url);
        }
    },

    async loadContent(url) {
        try {
            const response = await fetch(url);
            const text = await response.text();
            const parser = new DOMParser();
            const doc = parser.parseFromString(text, 'text/html');

            const newContent = doc.getElementById('main-content');
            const currentContent = document.getElementById('main-content');

            if (newContent && currentContent) {
                currentContent.replaceWith(newContent);
                document.title = doc.title;
                this.updateNavbar(url);

                // Execute scripts in the new content
                newContent.querySelectorAll('script').forEach(oldScript => {
                    const newScript = document.createElement('script');
                    Array.from(oldScript.attributes).forEach(attr => newScript.setAttribute(attr.name, attr.value));
                    newScript.appendChild(document.createTextNode(oldScript.innerHTML));
                    oldScript.parentNode.replaceChild(newScript, oldScript);
                });
            } else {
                window.location.reload();
            }
        } catch (err) {
            console.error('Navigation failed', err);
            window.location.href = url;
        }
    },

    updateNavbar(url) {
        const path = new URL(url).pathname;
        document.querySelectorAll('.nav-links a').forEach(a => {
            // loose matching
            if (a.getAttribute('href') === path) {
                a.classList.add('active');
            } else {
                a.classList.remove('active');
            }
        });
    }
};

document.addEventListener('DOMContentLoaded', () => {
    App.init();
});

// Helper for play button binding
// Helper for play button binding (called from inline onclicks or refined)
function playSongFromUI(id, title, artist, coverId) {
    // Collect all playable items on the current page to form a queue context
    const buttons = document.querySelectorAll('button.play-btn, button[onclick^="playSongFromUI"]');
    const queue = [];

    buttons.forEach(btn => {
        let sid, stitle, sartist, scoverId;

        if (btn.hasAttribute('data-id')) {
            sid = parseInt(btn.getAttribute('data-id'));
            stitle = btn.getAttribute('data-title');
            sartist = btn.getAttribute('data-artist');
            const cid = btn.getAttribute('data-album-id');
            scoverId = (cid && cid !== 'null') ? parseInt(cid) : null;
        } else {
            const onClick = btn.getAttribute('onclick');
            const match = onClick.match(/^playSongFromUI\((.*)\)$/);
            if (match) {
                const argsStr = match[1];
                const args = [];
                let currentArg = '';
                let inQuote = false;
                let quoteChar = '';

                for (let i = 0; i < argsStr.length; i++) {
                    const char = argsStr[i];
                    if ((char === "'" || char === '"') && (i === 0 || argsStr[i - 1] !== '\\')) {
                        if (!inQuote) {
                            inQuote = true;
                            quoteChar = char;
                        } else if (char === quoteChar) {
                            inQuote = false;
                        } else {
                            currentArg += char;
                        }
                    } else if (char === ',' && !inQuote) {
                        args.push(currentArg.trim());
                        currentArg = '';
                    } else {
                        currentArg += char;
                    }
                }
                args.push(currentArg.trim());

                const cleanArgs = args.map(arg => {
                    if ((arg.startsWith("'") && arg.endsWith("'")) || (arg.startsWith('"') && arg.endsWith('"'))) {
                        return arg.substring(1, arg.length - 1);
                    }
                    if (arg === 'null') return null;
                    return arg;
                });

                sid = parseInt(cleanArgs[0]);
                stitle = cleanArgs[1];
                sartist = cleanArgs[2];
                scoverId = cleanArgs[3] && cleanArgs[3] !== 'null' ? parseInt(cleanArgs[3]) : null;
            }
        }

        if (sid) {
            queue.push({ id: sid, title: stitle, artist: sartist, coverId: scoverId });
        }
    });

    if (queue.length === 0) {
        queue.push({ id, title, artist, coverId });
    }

    // Deduplicate queue
    const uniqueQueue = [];
    const seen = new Set();
    for (const s of queue) {
        if (!seen.has(s.id)) {
            seen.add(s.id);
            uniqueQueue.push(s);
        }
    }

    Player.playSong(id, uniqueQueue);
}

function playSongFromData(el) {
    const id = parseInt(el.getAttribute('data-id'));
    const title = el.getAttribute('data-title');
    const artist = el.getAttribute('data-artist');
    const coverId = el.getAttribute('data-album-id');
    const parsedCoverId = (coverId && coverId !== 'null') ? parseInt(coverId) : null;
    playSongFromUI(id, title, artist, parsedCoverId);
}

// Global Event Delegation for play buttons
document.addEventListener('click', (e) => {
    const playBtn = e.target.closest('.play-btn');
    if (playBtn) {
        playSongFromData(playBtn);
    }
});

window.updateFileName = function (input) {
    const label = input.nextElementSibling;
    if (input.files && input.files.length > 0) {
        label.innerText = '✔️ ' + input.files[0].name;
        label.style.borderColor = 'var(--primary)';
        label.style.color = 'var(--primary)';
    } else {
        label.innerText = '📁 Choose File';
        label.style.borderColor = 'var(--primary)';
        label.style.color = 'var(--text-secondary)';
    }
};
