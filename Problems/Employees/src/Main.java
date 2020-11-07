class Employee {

    // write fields
    final String name;
    ;
    final String email;
    final Integer experience;

    // write constructor

    public Employee(String name, String email, Integer experience) {
        this.name = name;
        this.email = email;
        this.experience = experience;
    }

    // write getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getExperience() {
        return experience;
    }
}

class Developer extends Employee {

    // write fields
    final String mainLanguage;
    final String[] skills;

    // write constructor
    Developer(String name, String email, Integer experience, String mainLanguage, String[] skills) {
        super(name, email, experience);
        this.mainLanguage = mainLanguage;
        this.skills = skills;
    }

    // write getters
    public String getMainLanguage() {
        return mainLanguage;
    }

    public String[] getSkills() {
        return skills;
    }
}

class DataAnalyst extends Employee {
    // write fields
    final boolean phd;
    final String[] methods;

    // write constructor
    DataAnalyst(String name, String email, Integer experience, boolean phd, String[] methods) {
        super(name, email, experience);
        this.phd = phd;
        this.methods = methods;
    }

    // write getters
    public boolean isPhd() {
        return phd;
    }

    public String[] getMethods() {
        return methods;
    }
}
