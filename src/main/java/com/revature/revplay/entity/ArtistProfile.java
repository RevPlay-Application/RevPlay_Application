package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * This JPA entity extends the base User record for members with the 'ARTIST'
 * role.
 * It houses specialized professional metadata that defines a creator's public
 * persona.
 * This includes high-resolution banner images, a professional stage name,
 * detailed creative biographies, and direct links to their external social
 * media presence.
 * By using a One-to-One mapping with Shared ID, it ensures that every artist
 * profile is strictly tethered to a valid user account while keeping the
 * base User table lightweight for standard listeners.
 */
@Entity
@Table(name = "artist_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// ####################################### Person2 CODE START #########################################
public class ArtistProfile {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String artistName;
    private String genre;
    
    @Lob
    private byte[] bannerImage;
    
    @Transient
    private String bannerImageUrl;
    
    public String getBannerImageUrl() {
        return (this.bannerImage != null) ? "/api/media/artist/" + this.id + "/banner" : null;
    }
    
    @Column(columnDefinition = "CLOB")
    private String bio;
    
    private String instagramUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private String spotifyUrl;
    private String websiteUrl;
}

// ######################################## Person2 CODE END ##########################################
