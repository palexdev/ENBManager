package io.github.palexdev.enbmanager.backend.utils;

public class OSUtils {
    //================================================================================
    // Internal Classes
    //================================================================================
    public enum OSType {
        Windows, Linux, MacOS, Other
    }

    //================================================================================
    // Properties
    //================================================================================
    private static OSType osType;

    //================================================================================
    // Constructors
    //================================================================================
    private OSUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================
    public static boolean supportedPlatform() {
        return os() != OSType.Other;
    }

    public static OSType os() {
        if (osType == null) {
            String name = System.getProperty("os.name");
            if (name.contains("win") || name.contains("Win")) osType = OSType.Windows;
            else if (name.contains("nix") || name.contains("nux")) osType = OSType.Linux;
                //if (name.contains("mac")) return OSType.MacOS; // MacOS is not supported, afaik no one games on it anyway
            else osType = OSType.Other;
        }
        return osType;
    }

    public static boolean isProcessRunning(String name) {
        return ProcessHandle.allProcesses()
            .parallel()
            .anyMatch(p -> p.info().command().map(s -> s.endsWith(name)).orElse(false));
    }
}
