class Box<T extends Animal> {
    private T container;

    public void add(T object) {
        this.container = object;
    }
}

// Don't change the code below
class Animal {
}
