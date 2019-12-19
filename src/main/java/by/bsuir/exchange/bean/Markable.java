package by.bsuir.exchange.bean;

/**
 * The interface Markable provides operations for
 * tracking components.
 */
public interface Markable {
    /**
     * Gets the unique identifier of a component in its domain.
     */
    long getId();

    /**
     * Sets the unique identifier for a component.
     * @param id the unique id
     */
    void setId(long id);
}
