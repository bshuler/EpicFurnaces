package com.songoda.epicfurnaces.api;


import org.bukkit.ChatColor;

/**
 * The access point of the EpicFurnacesAPI, a class acting as a bridge between API
 * and plugin implementation. It is from here where developers should access the
 * important and core methods in the API. All static methods in this class will
 * call directly upon the implementation at hand (in most cases this will be the
 * EpicFurnaces plugin itself), therefore a call to {@link #getImplementation()} is
 * not required and redundant in most situations. Method calls from this class are
 * preferred the majority of time, though an instance of {@link EpicFurnaces} may
 * be passed if absolutely necessary.
 *
 * @see EpicFurnaces
 * @since 3.0.0
 */
public class EpicFurnacesAPI {

    private static EpicFurnaces implementation;

    /**
     * Set the EpicFurnaces implementation. Once called for the first time, this
     * method will throw an exception on any subsequent invocations. The implementation
     * may only be set a single time, presumably by the EpicFurnaces plugin
     *
     * @param implementation the implementation to set
     */
    public static void setImplementation(EpicFurnaces implementation) {
        if (EpicFurnacesAPI.implementation != null) {
            throw new IllegalArgumentException("Cannot set API implementation twice");
        }

        EpicFurnacesAPI.implementation = implementation;
    }

    /**
     * Get the EpicFurnaces implementation. This method may be redundant in most
     * situations as all methods present in {@link EpicFurnaces} will be mirrored
     * with static modifiers in the {@link EpicFurnacesAPI} class
     *
     * @return the EpicFurnaces implementation
     */
    public static EpicFurnaces getImplementation() {
        return implementation;
    }
}
