package com.interviewprep.web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Topic model - same structure as the desktop app but without JavaFX dependencies.
 */
public class Topic {
    private String title;
    private String category;
    private String definition;
    private String whyItMatters;
    private String howItWorks;
    private String internalWorking;
    private String syntax;
    private String codeExample;
    private String realWorldExample;
    private List<String> advantages;
    private List<String> disadvantages;
    private String whenToUse;
    private String whenNotToUse;
    private List<String> commonMistakes;
    private String interviewAnswer;
    private List<String> followUpQuestions;
    private List<String> scenarioQuestions;
    private String quickRevision;
    private String difficulty;

    public Topic(String title) {
        this.title = title;
        this.advantages = new ArrayList<>();
        this.disadvantages = new ArrayList<>();
        this.commonMistakes = new ArrayList<>();
        this.followUpQuestions = new ArrayList<>();
        this.scenarioQuestions = new ArrayList<>();
        this.difficulty = "Intermediate";
    }

    // Builder pattern
    public Topic definition(String v) { this.definition = v; return this; }
    public Topic whyItMatters(String v) { this.whyItMatters = v; return this; }
    public Topic howItWorks(String v) { this.howItWorks = v; return this; }
    public Topic internalWorking(String v) { this.internalWorking = v; return this; }
    public Topic syntax(String v) { this.syntax = v; return this; }
    public Topic codeExample(String v) { this.codeExample = v; return this; }
    public Topic realWorldExample(String v) { this.realWorldExample = v; return this; }
    public Topic advantages(List<String> v) { this.advantages = v; return this; }
    public Topic disadvantages(List<String> v) { this.disadvantages = v; return this; }
    public Topic whenToUse(String v) { this.whenToUse = v; return this; }
    public Topic whenNotToUse(String v) { this.whenNotToUse = v; return this; }
    public Topic commonMistakes(List<String> v) { this.commonMistakes = v; return this; }
    public Topic interviewAnswer(String v) { this.interviewAnswer = v; return this; }
    public Topic followUpQuestions(List<String> v) { this.followUpQuestions = v; return this; }
    public Topic scenarioQuestions(List<String> v) { this.scenarioQuestions = v; return this; }
    public Topic quickRevision(String v) { this.quickRevision = v; return this; }
    public Topic category(String v) { this.category = v; return this; }
    public Topic difficulty(String v) { this.difficulty = v; return this; }

    // Getters
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDefinition() { return definition; }
    public String getWhyItMatters() { return whyItMatters; }
    public String getHowItWorks() { return howItWorks; }
    public String getInternalWorking() { return internalWorking; }
    public String getSyntax() { return syntax; }
    public String getCodeExample() { return codeExample; }
    public String getRealWorldExample() { return realWorldExample; }
    public List<String> getAdvantages() { return advantages; }
    public List<String> getDisadvantages() { return disadvantages; }
    public String getWhenToUse() { return whenToUse; }
    public String getWhenNotToUse() { return whenNotToUse; }
    public List<String> getCommonMistakes() { return commonMistakes; }
    public String getInterviewAnswer() { return interviewAnswer; }
    public List<String> getFollowUpQuestions() { return followUpQuestions; }
    public List<String> getScenarioQuestions() { return scenarioQuestions; }
    public String getQuickRevision() { return quickRevision; }
    public String getDifficulty() { return difficulty; }
}
