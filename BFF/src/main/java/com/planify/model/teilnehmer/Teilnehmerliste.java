
package com.planify.model.teilnehmer;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "event_id", "user_id", "role", "status"
})

public class Teilnehmerliste {
    @JsonProperty("event_id")
    private int event_id;
    @JsonProperty("user_id")
    private int user_id;
    @JsonProperty("role")
    private String role;
    @JsonProperty("status")
    private String status;
    @JsonProperty("email")
    private String email;

    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public Teilnehmerliste() {
    }

    public Teilnehmerliste(int event_id, int user_id, String role, String status, String email) {
        this.event_id = event_id;
        this.user_id = user_id;
        this.role = role;
        this.status = status;
        this.email = email;
    }

    @JsonProperty("event_id")
    public int getEventId() {
        return event_id;
    }

    @JsonProperty("event_id")
    public void setEventId(int event_id) {
        this.event_id = event_id;
    }

    @JsonProperty("user_id")
    public int getUserId() {
        return user_id;
    }

    @JsonProperty("user_id")
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }
    
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Teilnehmerliste.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("event_id");
        sb.append('=');
        sb.append(((this.event_id == 0)?"<null>":"" + this.event_id));
        sb.append(',');
        sb.append("user_id");
        sb.append('=');
        sb.append(((this.user_id == 0)?"<null>":"" + this.user_id));
        sb.append(',');
        sb.append("role");
        sb.append('=');
        sb.append(((this.role == null)?"<null>":this.role));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
        sb.append(',');
        sb.append("email");
        sb.append('=');
        sb.append(((this.email == null)?"<null>":this.email));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
