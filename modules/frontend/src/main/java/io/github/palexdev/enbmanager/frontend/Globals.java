package io.github.palexdev.enbmanager.frontend;

@SuppressWarnings("TextBlockMigration")
public class Globals {
    //================================================================================
    // Constants
    //================================================================================
    public static final String ABOUT =
        "# About\n" +
            "ENBManager is an app that enables easy management of your ENB configurations for any supported game. " +
            "Developed using Java and JavaFX, this application is compatible with major platforms such as Windows, Mac OS, and Linux. Moreover, it is an " +
            "open-source project, allowing everyone to contribute, support, report issues, fork the code, and make modifications on [GitHub](https://github.com/palexdev/ENBManager).";
    public static final String WHY =
        "\u2003  \n" +
            "### Why yet another ENB manager tool?\n" +
            "\u2003  \n" +
            "**TLDR: The tool I previously used didn't work well on Linux, so I developed my own out of necessity and for the joy of coding..**\n" +
            "\u2003  \n" +
            "\u2003  \n" +
            "A few years ago, I switched to Linux on all of my PCs. The introduction of the earliest versions of Windows 11 had led me to a breaking point, " +
            "as I could no longer tolerate the clutter, bugs, and overall way of managing things of the operating system.\n" +
            "\u2003  \n" +
            "Recently, I decided to revisit Skyrim and delve into extensive modding, particularly focusing on graphics enhancements. " +
            "When it came to choosing an ENB preset, I found myself overwhelmed by the vast array of options available. To maintain my sanity and simplify the selection process, " +
            "I immediately went on the Nexus to download the fantastic [ENBManager](https://www.nexusmods.com/skyrimspecialedition/mods/4143) tool developed by **volvaga0** " +
            "(by the way, a big thank you to this guy, as its work has saved me a tremendous amount of time in the past).\n" +
            "\u2003  \n" +
                "Unfortunately, the tool didn't function properly on Linux. While the core functionalities might have worked, the issue was primarily about " +
                "the graphical user interface (GUI). Specifically, the buttons responsible for saving/loading configurations were not visible. " +
                "This left me with two options:\n\n" +
                "1) Manually undertake the entire process (which was less than ideal, to say the least).\n" +
                "2) Develop my own tool.\n\n" +
                "\u2003  \n" +
                "And so I did, I embarked on the latter option. After all, I love programming and I saw this as a fantastic opportunity to have fun, improve my skills and test some of my " +
                "UI libraries I'm developing.\n" +
                "\u2003  \n" +
                "To be honest, I initially estimated that it would take around 2 to 3, maybe 4 days, to complete the development... " +
                "But as it turned out, it ended up consuming a full three weeks of my time... Well, to be precise the app took only one week, distributing it has been a total nightmare" +
                "thanks to Java. ¯\\_(ツ)_/¯";
    public static final String FUNCTIONALITIES =
            "# Functionalities\n" +
                    "ENBManager offers the same core functionalities as any other tool, with minimal differences. In theory, it should also support ReShade, although I have personally only tested it with ENB since that's what I use.\n" +
                    "\u2003  \n" +
                    "\u2003  \n" +
                    "When you first launch the app, a window will prompt you to select the game you wish to manage, along with its installation path. " +
                    "Currently, there is no support for automatically reading registry keys to obtain the path. However, I believe manually specifying the path is a more robust and reliable approach, so I do not plan on adding automatic detection. " +
                    "Rest assured, you won't need to repeat this process every time. In the same window, there is an option to remember the game and its path for future sessions.\n" +
                    "\u2003  \n" +
                    "\u2003  \n" +
            "Once the initial setup is complete, you will be presented with the main window, which serves as a container for the other three sections:\n" +
            "1) The main window also displays a rounded icon in the top left corner, indicating the currently managed game. Clicking on this icon allows you to switch games using the same dialog shown during the initial startup. " +
            "On the left side, a sidebar with buttons allows you to navigate between sections.\n" +
            "2) The Home section displays the ENB files detected in the game's installation folder. The table automatically updates when changes are detected on the disk. " +
            "You can individually select files or select all at once, and at the bottom, you will find options to either save or delete the selected files.\n" +
            "3) The Settings section contains app-related settings as well as settings specific to the currently managed game. Here, you can customize the app theme (currently offering only two options) and modify the game path if necessary.\n" +
            "4) The Repo section displays the list of saved configurations for the currently managed game. Here, you can load or delete configurations and check which files are included in them.\n" +
            "The save path depends on the operating system:\n\n" +
            "%sa) On Windows: %%APPDATA%%\\ENBManager\n\n".formatted(spaces(8)) +
            "%sb) On MacOS: ~/Library/Application Support/ENBManager\n\n".formatted(spaces(8)) +
            "%sc) On Linux: ~/.config/ENBManager\n".formatted(spaces(8));
    public static String TOOLS =
        "# Tools\n" +
            "The realization of ENBManager was possible thanks to these great tools and libraries:\n" +
            "- [JavaFX](https://github.com/openjdk/jfx)\n" +
            "- [MaterialFX](https://github.com/palexdev/MaterialFX)\n" +
            "- [MFXComponents](https://github.com/palexdev/MaterialFX/tree/rewrite)\n" +
            "- [MFXCore](https://github.com/palexdev/MaterialFX/tree/rewrite)\n" +
            "- [MFXEffects](https://github.com/palexdev/MaterialFX/tree/rewrite)\n" +
            "- [MFXResources](https://github.com/palexdev/MaterialFX/tree/rewrite)\n" +
            "- [VirtualizedFX](https://github.com/palexdev/VirtualizedFX)\n" +
            "- [markdown-javafx-renderer](https://github.com/JPro-one/markdown-javafx-renderer)\n" +
            "- [CSSFX](https://github.com/McFoggy/cssfx)\n" +
            "- [ScenivView](https://github.com/JonathanGiles/scenic-view)\n" +
            "- [Inverno](https://inverno.io)\n" +
            "- [DirectoryWatcher](https://github.com/gmethvin/directory-watcher)\n" +
            "- [IntelliJ_Idea](https://www.jetbrains.com/idea/)\n";
    public static String SUPPORT =
        "# Support\n" +
            "You can support this project in various ways:\n" +
            "1) You can endorse it on the [Nexus](https://www.nexusmods.com/)\n" +
            "2) You can drop by on [GitHub](https://github.com/palexdev/ENBManager/discussions) to say hello\n" +
            "3) If you feel like it, you can pay me a coffee here [PayPal](https://www.paypal.com/paypalme/alxpar404/2)";


    // TODO update on release
    //================================================================================
    // Constructors
    //================================================================================
    private Globals() {}

    //================================================================================
    // Methods
    //================================================================================
    private static String spaces(int n) {
        return "\u2003".repeat(n);
    }
}
