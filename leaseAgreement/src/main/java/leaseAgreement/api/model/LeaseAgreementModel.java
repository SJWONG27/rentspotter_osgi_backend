package leaseAgreement.api.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.io.Serializable;

public class LeaseAgreementModel implements Serializable {
    @BsonId
    private ObjectId id;


    private String tenantId;
    private String landlordId;
    private String propertyId;
    private String applicationId;

    private LeaseAgreementStatus leaseStatus = LeaseAgreementStatus.NOT_APPLICABLE;

    private String day;
    private String month;
    private String year;
    private String lessorName;
    private String lessorIc;
    private String lesseeName;
    private String address;
    private String effectiveDate;
    private String expireDate;
    private String rentRmWord;
    private Double rentRmNum;
    private Integer advanceDay;
    private String depositRmWord;
    private Double depositRmNum;
    private String lessorAdd;
    private String lessorTel;
    private String lessorFax;
    private String lesseeAdd;
    private String lesseeTel;
    private String lesseeFax;
    private String lessorDesignation;
    private String lessorSignature;
    private String lesseeIc;
    private String lesseeDesignation;
    private String lesseeSignature;
    private Boolean completed = false;
    private String pdf; // Base64 String

    public LeaseAgreementModel() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public LeaseAgreementStatus getLeaseStatus() {
        return leaseStatus;
    }

    public void setLeaseStatus(LeaseAgreementStatus leaseStatus) {
        this.leaseStatus = leaseStatus;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLessorName() {
        return lessorName;
    }

    public void setLessorName(String lessorName) {
        this.lessorName = lessorName;
    }

    public String getLessorIc() {
        return lessorIc;
    }

    public void setLessorIc(String lessorIc) {
        this.lessorIc = lessorIc;
    }

    public String getLesseeName() {
        return lesseeName;
    }

    public void setLesseeName(String lesseeName) {
        this.lesseeName = lesseeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getRentRmWord() {
        return rentRmWord;
    }

    public void setRentRmWord(String rentRmWord) {
        this.rentRmWord = rentRmWord;
    }

    public Double getRentRmNum() {
        return rentRmNum;
    }

    public void setRentRmNum(Double rentRmNum) {
        this.rentRmNum = rentRmNum;
    }

    public Integer getAdvanceDay() {
        return advanceDay;
    }

    public void setAdvanceDay(Integer advanceDay) {
        this.advanceDay = advanceDay;
    }

    public String getDepositRmWord() {
        return depositRmWord;
    }

    public void setDepositRmWord(String depositRmWord) {
        this.depositRmWord = depositRmWord;
    }

    public Double getDepositRmNum() {
        return depositRmNum;
    }

    public void setDepositRmNum(Double depositRmNum) {
        this.depositRmNum = depositRmNum;
    }

    public String getLessorAdd() {
        return lessorAdd;
    }

    public void setLessorAdd(String lessorAdd) {
        this.lessorAdd = lessorAdd;
    }

    public String getLessorTel() {
        return lessorTel;
    }

    public void setLessorTel(String lessorTel) {
        this.lessorTel = lessorTel;
    }

    public String getLessorFax() {
        return lessorFax;
    }

    public void setLessorFax(String lessorFax) {
        this.lessorFax = lessorFax;
    }

    public String getLesseeAdd() {
        return lesseeAdd;
    }

    public void setLesseeAdd(String lesseeAdd) {
        this.lesseeAdd = lesseeAdd;
    }

    public String getLesseeTel() {
        return lesseeTel;
    }

    public void setLesseeTel(String lesseeTel) {
        this.lesseeTel = lesseeTel;
    }

    public String getLesseeFax() {
        return lesseeFax;
    }

    public void setLesseeFax(String lesseeFax) {
        this.lesseeFax = lesseeFax;
    }

    public String getLessorDesignation() {
        return lessorDesignation;
    }

    public void setLessorDesignation(String lessorDesignation) {
        this.lessorDesignation = lessorDesignation;
    }

    public String getLessorSignature() {
        return lessorSignature;
    }

    public void setLessorSignature(String lessorSignature) {
        this.lessorSignature = lessorSignature;
    }

    public String getLesseeIc() {
        return lesseeIc;
    }

    public void setLesseeIc(String lesseeIc) {
        this.lesseeIc = lesseeIc;
    }

    public String getLesseeDesignation() {
        return lesseeDesignation;
    }

    public void setLesseeDesignation(String lesseeDesignation) {
        this.lesseeDesignation = lesseeDesignation;
    }

    public String getLesseeSignature() {
        return lesseeSignature;
    }

    public void setLesseeSignature(String lesseeSignature) {
        this.lesseeSignature = lesseeSignature;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
}
