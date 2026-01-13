package leaseAgreement.api.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class LeaseAgreement {
    @BsonId
    private ObjectId id;

    // Relationships (Pasted as Strings from Postman)
    private String tenantId;
    private String landlordId;
    private String propertyId;
    private String applicationId;

    // Dates
    private String day;
    private String month;
    private String year;
    private String effectiveDate;
    private String expireDate;

    // Parties Info
    private String lessorName;
    private String lessorIc;
    private String lesseeName;
    private String lesseeIc;
    private String address;

    // Financials
    private String rentRmWord;
    private double rentRmNum;
    private int advanceDay;
    private String depositRmWord;
    private double depositRmNum;

    // Contact & Signatures
    private String lessorAdd;
    private String lessorTel;
    private String lessorSignature;
    private String lesseeSignature;

    // PDF Data (Buffer in Node = byte[] in Java)
    private boolean completed = false;
    private byte[] completedPdfData;
    private String contentType;

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

    public String getLesseeIc() {
        return lesseeIc;
    }

    public void setLesseeIc(String lesseeIc) {
        this.lesseeIc = lesseeIc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRentRmWord() {
        return rentRmWord;
    }

    public void setRentRmWord(String rentRmWord) {
        this.rentRmWord = rentRmWord;
    }

    public double getRentRmNum() {
        return rentRmNum;
    }

    public void setRentRmNum(double rentRmNum) {
        this.rentRmNum = rentRmNum;
    }

    public int getAdvanceDay() {
        return advanceDay;
    }

    public void setAdvanceDay(int advanceDay) {
        this.advanceDay = advanceDay;
    }

    public String getDepositRmWord() {
        return depositRmWord;
    }

    public void setDepositRmWord(String depositRmWord) {
        this.depositRmWord = depositRmWord;
    }

    public double getDepositRmNum() {
        return depositRmNum;
    }

    public void setDepositRmNum(double depositRmNum) {
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

    public String getLessorSignature() {
        return lessorSignature;
    }

    public void setLessorSignature(String lessorSignature) {
        this.lessorSignature = lessorSignature;
    }

    public String getLesseeSignature() {
        return lesseeSignature;
    }

    public void setLesseeSignature(String lesseeSignature) {
        this.lesseeSignature = lesseeSignature;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public byte[] getCompletedPdfData() {
        return completedPdfData;
    }

    public void setCompletedPdfData(byte[] completedPdfData) {
        this.completedPdfData = completedPdfData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
