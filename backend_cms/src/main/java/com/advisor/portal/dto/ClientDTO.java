package com.advisor.portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date of Birth is required")
    private LocalDate dob;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String fatherName;
    private String motherName;

    @NotBlank(message = "Blood Group is required")
    private String bloodGroup;

    @NotBlank(message = "ID Type is required")
    private String idType;

    @NotBlank(message = "ID Number is required")
    private String idNumber;

    @NotNull(message = "Investment amount is required")
    @Positive(message = "Investment amount must be positive")
    private Double investmentAmount;

    @NotNull(message = "Investment duration is required")
    @Positive(message = "Duration must be positive")
    private Integer investmentDuration;

    @NotBlank(message = "Risk Tolerance is required")
    private String riskTolerance;

    private String policyPreference;

    private String riskCategory;

    private String documentName;

    @NotBlank(message = "Gender is required")
    private String gender;

    private String address;

    @NotBlank(message = "Annual income is required")
    private String annualIncome;

    @NotBlank(message = "Employment status is required")
    private String employmentStatus;

    private String approxNetWorth;

    private String existingLoans;

    private String investmentGoal;

    private String preferredAssets;

    private String liquidityNeed;

    @NotBlank(message = "Market drop reaction is required")
    private String marketDropReaction;

    @NotBlank(message = "Investment experience is required")
    private String investmentExperience;

    @NotNull(message = "KYC completed status is required")
    private Boolean kycCompleted;

    @NotBlank(message = "Politically exposed status is required")
    private String politicallyExposed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public Double getInvestmentAmount() { return investmentAmount; }
    public void setInvestmentAmount(Double investmentAmount) { this.investmentAmount = investmentAmount; }

    public Integer getInvestmentDuration() { return investmentDuration; }
    public void setInvestmentDuration(Integer investmentDuration) { this.investmentDuration = investmentDuration; }

    public String getRiskTolerance() { return riskTolerance; }
    public void setRiskTolerance(String riskTolerance) { this.riskTolerance = riskTolerance; }

    public String getPolicyPreference() { return policyPreference; }
    public void setPolicyPreference(String policyPreference) { this.policyPreference = policyPreference; }

    public String getRiskCategory() { return riskCategory; }
    public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(String annualIncome) { this.annualIncome = annualIncome; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getApproxNetWorth() { return approxNetWorth; }
    public void setApproxNetWorth(String approxNetWorth) { this.approxNetWorth = approxNetWorth; }

    public String getExistingLoans() { return existingLoans; }
    public void setExistingLoans(String existingLoans) { this.existingLoans = existingLoans; }

    public String getInvestmentGoal() { return investmentGoal; }
    public void setInvestmentGoal(String investmentGoal) { this.investmentGoal = investmentGoal; }

    public String getPreferredAssets() { return preferredAssets; }
    public void setPreferredAssets(String preferredAssets) { this.preferredAssets = preferredAssets; }

    public String getLiquidityNeed() { return liquidityNeed; }
    public void setLiquidityNeed(String liquidityNeed) { this.liquidityNeed = liquidityNeed; }

    public String getMarketDropReaction() { return marketDropReaction; }
    public void setMarketDropReaction(String marketDropReaction) { this.marketDropReaction = marketDropReaction; }

    public String getInvestmentExperience() { return investmentExperience; }
    public void setInvestmentExperience(String investmentExperience) { this.investmentExperience = investmentExperience; }

    public Boolean getKycCompleted() { return kycCompleted; }
    public void setKycCompleted(Boolean kycCompleted) { this.kycCompleted = kycCompleted; }

    public String getPoliticallyExposed() { return politicallyExposed; }
    public void setPoliticallyExposed(String politicallyExposed) { this.politicallyExposed = politicallyExposed; }
}
