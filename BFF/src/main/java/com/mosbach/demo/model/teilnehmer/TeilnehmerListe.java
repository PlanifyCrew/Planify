
package com.mosbach.demo.model.teilnehmer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cglib.core.Local;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tnListe"
})

public class TeilnehmerListe {
    @JsonProperty("tnListe")
    private List<String> tnListe = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public TeilnehmerListe() {
    }

    public TeilnehmerListe(List<String> tnListe) {
        super();
        this.tnListe = tnListe;
    }

    @JsonProperty("tnListe")
    public List<String> getTnListe() {
        return tnListe;
    }

    @JsonProperty("tnListe")
    public void setTnListe(List<String> tnListe) {
        this.tnListe = tnListe;
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
        sb.append(TeilnehmerListe.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("tnListe");
        sb.append('=');
        sb.append(((this.tnListe == null)?"<null>":this.tnListe));
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
