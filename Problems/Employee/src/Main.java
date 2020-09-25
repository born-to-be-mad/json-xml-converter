class Employee {

    String name;
    int salary;
    String address;

    public Employee() {
        name = "unknown";
        address = "unknown";
        salary = 0;
    }

    public Employee(String name, int salary) {
        this.name = name;
        address = "unknown";
        this.salary = salary;
    }

    public Employee(String name, int salary, String address) {
        this.name = name;
        this.salary = salary;
        this.address = address;
    }
}
