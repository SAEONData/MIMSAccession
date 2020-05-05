package org.saeon.mims.accession.model.accession;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.util.AppUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;  

import static javax.persistence.CascadeType.ALL;

@Entity
public class Accession {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter @Setter
    private String accessionID;

    @Getter @Setter
    private Long accessionNumber;

    @Getter @Setter
    private String uuid;

    //@Min(1) @NotNull 
    @Size(min=1)
    @Getter @Setter
    private String name;

    @Getter @Setter
    private Long parentAccessionNumber;

    //@Min(1) @NotNull
    @Size(min=1)
    @Getter @Setter
    private String homeFolder;

    @Getter @Setter
    private String archiveFolder;

    @Getter
    @Setter
    @ManyToMany(cascade=ALL, mappedBy="accession")
    private List<FileChecksum> files;

    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    private EmbargoType embargoState;

    @Getter
    @Setter
    private Date embargoExpiry;

    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.NOT_STARTED;

    /******************************************
     *
     * UTILITY METHODS
     *
     ******************************************/

    public String getEmbargoState(){
        String embargoState = "";
        if (this.embargoState != null)
            embargoState = this.embargoState.getName();
        return embargoState;
    }

    public EmbargoType getEmbargoStateOriginal(){
        return this.embargoState;
    }

    
    public String getEmbargoExpiry() {
        String dateString = "";
        if (this.embargoExpiry != null){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
            dateString = dateFormat.format(this.embargoExpiry);
        }
        return dateString;
    }

    public Date getEmbargoExpiryDate(){
        return this.embargoExpiry;
    }

    public void setEmbargoExpiry(String date) {
        if (StringUtils.isNotEmpty(date)) {
            LocalDate d = AppUtils.convertStringToDate(date);
            this.embargoExpiry = Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else 
            this.embargoExpiry = null;
    }

    public void setEmbargoState(String embargoType) {
        this.embargoState = EmbargoType.getById(Integer.parseInt(embargoType));
    }

    public void setEmbargoStateWithType(EmbargoType embargoType){
        this.embargoState = embargoType;
    }
}
