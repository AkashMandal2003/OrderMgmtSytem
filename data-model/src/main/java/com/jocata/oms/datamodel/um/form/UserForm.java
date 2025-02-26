package com.jocata.oms.datamodel.um.form;

import jakarta.persistence.Column;

import java.util.Set;

public class UserForm {

    private String userId;

    private String fullName;

    private String email;

    private String passwordHash;

    private String phone;

    private String profilePicture;

    private String otpSecret;

    private Set<AddressForm> addresses;

    private Set<RoleForm> roles;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getOtpSecret() {
        return otpSecret;
    }

    public void setOtpSecret(String otpSecret) {
        this.otpSecret = otpSecret;
    }

    public Set<RoleForm> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleForm> roles) {
        this.roles = roles;
    }

    public Set<AddressForm> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<AddressForm> addresses) {
        this.addresses = addresses;
    }
}
