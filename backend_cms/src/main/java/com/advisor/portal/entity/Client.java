package com.advisor.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "investment_amount")
    private Double investmentAmount;

    @Column(name = "investment_duration")
    private Integer investmentDuration;

    @Column(name = "risk_tolerance")
    private String riskTolerance;

    @Column(name = "policy_preference")
    private String policyPreference;

    @Column(name = "risk_category")
    private String riskCategory;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "annual_income")
    private String annualIncome;

    @Column(name = "employment_status")
    private String employmentStatus;

    @Column(name = "approx_net_worth")
    private String approxNetWorth;

    @Column(name = "existing_loans")
    private String existingLoans;

    @Column(name = "investment_goal")
    private String investmentGoal;

    @Column(name = "preferred_assets")
    private String preferredAssets;

    @Column(name = "liquidity_need")
    private String liquidityNeed;

    @Column(name = "market_drop_reaction")
    private String marketDropReaction;

    @Column(name = "investment_experience")
    private String investmentExperience;

    @Column(name = "kyc_completed")
    private Boolean kycCompleted;

    @Column(name = "politically_exposed")
    private String politicallyExposed;

    @Lob
    @Column(name = "document_data", columnDefinition = "LONGBLOB")
    private byte[] documentData;
    
    @Column(name = "document_name")
    private String documentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    @JsonIgnore
    private Agent agent;

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

    public byte[] getDocumentData() { return documentData; }
    public void setDocumentData(byte[] documentData) { this.documentData = documentData; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }

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
