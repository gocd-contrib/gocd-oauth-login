package com.tw.go.plugin;

public class User {
    private String username;
    private String displayName;
    private String emailId;

    public User(String username, String displayName, String emailId) {
        this.username = username;
        this.displayName = displayName;
        setEmailId(emailId);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName != null && displayName.length() > 0 ? displayName: username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId == null ? null : emailId.toLowerCase().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (displayName != null ? !displayName.equals(user.displayName) : user.displayName != null) return false;
        if (emailId != null ? !emailId.equals(user.emailId) : user.emailId != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (emailId != null ? emailId.hashCode() : 0);
        return result;
    }
}
