/**
 * Class to work with
 */
class Multiplicator {

    /**
     * Multiply folders and put copies of original folder argument content in each.
     *
     * @param folder    folder which content should be multiplicated
     * @param arraySize size of array to return.
     *                  Each array element should have Folder with copy of the original content inside
     * @return array of Folder<T>[] objects
     */
    public static <T extends Copy<T>> Folder<T>[] multiply(Folder<T> folder, int arraySize) {
        Folder[] result = new Folder[arraySize];
        T originalFolderContent = folder.get();
        for (int i = 0; i < arraySize; i++) {
            Folder<T> newFolder = new Folder<>();
            newFolder.put(originalFolderContent.copy());
            result[i] = newFolder;
        }

        return result;
    }

}

// Don't change the code below
interface Copy<T> {
	T copy();
}

class Folder<T> {

    private T item;

    public void put(T item) {
    	this.item = item;
    }

    public T get() {
        return this.item;
    }
}
