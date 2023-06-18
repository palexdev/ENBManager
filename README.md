<a name="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
<h3 align="center">ENBManager</h3>

  <p align="center">
    A cross-platform tool to manage your ENB and ReShade configurations
    <br />
    <a href="https://github.com/palexdev/ENBManager/wiki"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/palexdev/ENBManager/issues">Report Bug</a>
    ·
    <a href="https://github.com/palexdev/ENBManager/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li><a href="#versioning">Versioning</a></li>
    <li><a href="#features-and-usage">Features and Usage</a></li>
    <li><a href="#configs-and-files-detection">Configs and Files Detection</a></li>
    <li><a href="#games-support">Games Support</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="theming">Theming</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#support">Support</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

[![ENBManager][product-screenshot]](#about-the-project)

Finding the best graphic configuration for your new modded journey can be quite frustrating when the possibilities are
vast.
ENBManager wants to make your life easier, allowing to load and save ENB and ReShade configurations with just a few
clicks.
Its UI wants to make you conformable, it's elegant, up to modern standards, intuitive and yet rich of features.

There already are many great tools on the Nexus for the same job. Shutout to  **volvaga0** for its
amazing [ENBManager](https://www.nexusmods.com/skyrimspecialedition/mods/4143)
tool that saved my a lot of time in the past. So you may ask: **Why?**

My tool has several advantages. One already mentioned, the UI. I'm not a designer, but I really like fiddling with '
frontend'
stuff. Also, the app is cross-platform thanks to the **Java Language**, meaning that it will reliably work on all
supported
OSes. A few years ago, I decided to ditch Windows once and for all, and so I transitioned to Linux on all my PCs.
Unfortunately,
not all tools work great on Linux, especially the ones built with Microsoft's .NET.

So, out of need, and because I love coding, I decided to make my own tool.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* **[Java](https://www.java.com)**
* **[JavaFX](https://github.com/openjdk/jfx)**
* **[MaterialFX](https://github.com/palexdev/MaterialFX)**
* **[MFXComponents](https://github.com/palexdev/MaterialFX/tree/rewrite)**
* **[MFXCore](https://github.com/palexdev/MaterialFX/tree/rewrite)**
* **[MFXEffects](https://github.com/palexdev/MaterialFX/tree/rewrite)**
* **[MFXResources](https://github.com/palexdev/MaterialFX/tree/rewrite)**
* **[VirtualizedFX](https://github.com/palexdev/VirtualizedFX)**
* **[markdown-javafx-renderer](https://github.com/JPro-one/markdown-javafx-renderer)**
* **[CSSFX](https://github.com/McFoggy/cssfx)**
* **[ScenicView](https://github.com/JonathanGiles/scenic-view)**
* **[Inverno](https://inverno.io)**
* **[DirectoryWatcher](https://github.com/gmethvin/directory-watcher)**
* **[IntelliJ Idea](https://www.jetbrains.com/idea/)**

<div align="center">
    <img src="https://inverno.io/img/inverno_portable.svg" width="240" alt=""/>
</div>

<p align="center"><strong>Shutout to the <a href="https://inverno.io">Inverno</a> framework, the one and only DI framework among many giants that made this project possible</strong></p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- VERSIONING -->

## Versioning

As you may have noticed, I adopt a quite strange versioning strategy. In reality, it is a variation of the
standard [Semver](https://semver.org/).  
The version is separated in three parts:

1) The first number represents the Java SDK version used to compile and run the app
2) The second number is the MAJOR version. Changes of this number represent addition/removal of features, and in general
   any major change to the code base
3) The third number id the MINOR version. Changes to this number represent the fix of minor issues or the
   addition/removal/change
   of minor details in the codebase

<!-- FEATURES AND USAGE -->

## Features and Usage

The app's functionalities can be break down in 4 main sections:

1) **Main Window:** the main window is the container/root on which the other sections will appear when switching through
   the buttons on the sidebar. In the top left corner, there's an image showing the currently managed game. By clicking
   on
   that, you are allowed to change the game.
2) **Home Section:** the home section is the view you will always see at the start of the app. Here's a table will show
   you which files have been detected in the game's folder. Selection boxes on the left allow you to choose which files
   to
   back up/delete. A selection box on the left most column allows you to quickly select/deselect all the detected files.
   On the bottom, a series of buttons allow you to perform some actions. In order, you can:
   1) Autosize the table's columns, to make all file names fit
   2) Refresh the detected files. When a change occur in the game directory, the table automatically updates its state,
      so this is not needed normally. If for whatever reason the new files are not detected you can use this button to
      force it
   3) Save the selected files in a new named config (this is enabled only if files are selected in the table)
   4) Delete the selected files (this is enabled only if files are selected in the table)  
      More info on configs and files detection: [Configs and Files Detection](#configs-and-files-detection)
3) **Repo Section:** The Repo section displays the list of saved configurations for the currently managed game.
   Here, you can load or delete configurations and check which files are included in them.
4) **Settings Section:** The Settings section contains app-related settings as well as settings specific to the
   currently managed game.
   Here, you can customize the app theme as well as change the game directory if necessary.
   Currently, there are only two themes to choose between, adding other colors is fairly simple and I plan to do it in
   the
   future. Some more info here: [Theming](#theming)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONFIGS AND FILES DETECTION -->

## Configs and Files Detection

For the app to work properly there are two hard requirements:

1) The OS, either Linux or Windows, MacOS is not supported as I don't think its worth it,
   but if anyone needs it feel free to open an issue. Thanks to **Java** adding the supports is super easy.
2) The app uses two paths, these must be present otherwise it will crash:
   1) The `Config Directory`, a path at which configs are stored: on Windows this is at `%%APPDATA%%\ENBManager\Game`,
      on Linux it is at `~/.config/ENBManager/Game`
   2) The `Cache Directory`, a path at which cache files are stored: on Windows this is
      at `%%APPDATA%%\Cache\ENBManager`,
      on Linux it is at `~/.cache/ENBManager`

(Bonus: the app stores its logs in the OS temporary directory: on Windows `...\AppData\Local\Tmp`, on Linux `/tmp`)

<br />
<br />

Once the game directory is set (or changed in the settings) an automatic system will detect all the files that are
related
to ENB or ReShade. The list of files is hardcoded, but can be changed after the first startup. The list is cached in a
text file in the `Config Directory`. When the file is present, the names will be parsed from it, so: you can delete the
file
to reset the detection system, you can alter the file to add/remove some from the detection.

_I tried to include every file possible related to the two graphical extensions. I may have missed some. If that's the
case
feel free to submit an issue or contact me and I will add them._

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- GAMES SUPPORT -->

## Games Support

At the time of writing this, ENBManager only supports two games:

1) Skyrim
2) Skyrim Special Edition

That is because those are the only ones I'm playing. However, adding new games should be fairly simple. You can open an
issue here on [GitHub](https://github.com/palexdev/ENBManager/issues), there is already a template for this. I
essentially
need three things:

1) An icon for the game, it should be a 128x128 PNG image
2) The game's 'full' name
3) The game's executable name

<!-- ROADMAP -->

## Roadmap

- [ ] Add support for more games (need community help)
- [ ] Add more themes
- [ ] Add possibility to import/export configurations
- [ ] Add multi-language support
- [ ] Fix a bunch of minor bugs, as well as address some TODOs in the codebase

See the [open issues](https://github.com/othneildrew/Best-README-Template/issues) for a full list of proposed features (
and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- THEMING -->

## Theming

_This section is for users that want to make custom themes, it will require a lot of time and patience to learn how.
If you don't have any of these requirements I don't recommend you investing time on it_

ENBManager offers a series of predefined themes that can be switched at runtime in the blink of an eye in the settings
section. The reality however is much more complicated.  
The app is built with [JavaFX](https://github.com/openjdk/jfx). For those who never heard of the framework before, in
synthesis, it is the successor of Swing, the old GUI toolkit of Java. The peculiarity of this framework compared to many
others (especially on the desktop) is that the views/components can be styled by
using [CSS](https://en.wikipedia.org/wiki/CSS).
Well, to be precise, it uses a custom subset of the CSS specification, the specs can be found
here: [JavaFX CSS](https://openjfx.io/javadoc/20/javafx.graphics/javafx/scene/doc-files/cssref.html).

It is amazing. Customizing components is easy and fast once you understand how it works, which again, it's fairly easy
but time-consuming.

That said, now we can break down the app's themes in three components:

1) JavaFX comes with two inbuilt themes (that kinda suck) called MODENA and CASPIAN. The first one is the latest, and
   it's
   a bit more modern and elegant than CASPIAN, but still pretty retro
2) The app's components (buttons, check boxes, etc...) mostly come from libraries that wrote and maintain. In
   particular,
   [MaterialFX](https://github.com/palexdev/MaterialFX/tree/rewrite) (beware, the one on the `rewrite` branch!), brings
   Material Design 3 components to JavaFX. Understanding how this works is **crucial** to make a custom theme.  
   Themes can be found
   here: [MFXResources](https://github.com/palexdev/MaterialFX/tree/rewrite/modules/resources/src/main/resources/io/github/palexdev/mfxresources/themes/material).
   Every theme, exposes a set of `tokens`, in simple terms, think about them as variables that can be reused in any part
   of the theme.
   Themes there, are written in SCSS, don't worry, it's CSS on steroids, it is compiled to CSS anyway. This is needed
   because like I said before
   the JavaFX CSS implementation is limited and variables do not exist. To be precise, there are lookups for the colors,
   but they
   are just for the colors and nothing else. These themes, follow Google's
   latest [Material Design Guidelines](https://m3.material.io/),
   the cool thing is that thanks to the `tokens` systems it is super super easy to create dark themes.
3) The app specific theme file,
   here: [Stylesheet](https://github.com/palexdev/ENBManager/blob/main/modules/frontend/src/main/resources/io/github/palexdev/enbmanager/frontend/css/AppTheme.css)

For technical reasons not worth explaining here, the app compiles a single CSS file from the three mentioned above. Then
they are cached
in the `Cache Directory` and reload from the disk if present the next time the app is started.

Now, if you take a look at one of these cached themes, you may be overwhelmed by the size of it. Don't worry, you
actually
have to consider only a portion of it. You have to skip all the MODENA section, and go to the MaterialFX section.

**Q:** _Where?._ You may ask.  
**A:** You can recognize this section easily as it always starts like this:

```CSS
.root {
   -md-source: . . .;
   -md-ref-palette . . .: . . .;
}
```

In this section are defined the look of every 'trivial' component such as: buttons, check boxes, toggles, popups,
etc...  
At the time of writing this, MaterialFX covers only so little. The thing is, the newest version is a `rewrite` of the
old
version available on the `main` branch. I'm working on it, but it requires a lot of time which lately I don't have haha.

The next section is the app specific theme, it starts with:

```css
.root {
   -red: #F44336;
   -orange: #FF9800;
   -yellow: #FBC02D;
   -blue: #2196F3;
   -green: #4CAF50;
   -purple: #9C27B0;
}
```

This part specifies the look of every section of the app: Main, Home, Repo, Settings, About. As well as defining the
look
of the window header, the window buttons, and customizing components that come from MaterialFX.

**Q:** _Wow so many information, but how can I make my custom theme?_  
**A:** So, you have two options:

1) ENBManager implements a system that watches for any change in the cached themes. If you modify the theme file (and of
   course
   you have that theme currently active) upon focusing the app window you will see the changes you made. This is great
   to
   create personal custom themes, or to fiddle around with the theming system, see what works and what doesn't. It's a
   great
   learning 'tool'. This system is quite flexible, in fact you could abuse it to create a custom theme entirely from
   scratch even.
   **However**, keep in mind that cache files may be deleted or overwritten, so my suggestion is to always keep a backup
   of
   your modifications if anything happens
2) If you want to see more colors, send me the HEX value. To be precise, I would need three colors: the primary (the one
   you want to see),
   the secondary and the tertiary. They should be harmonious with each other, from these three I can generate new
   Material Design 3 themes. You can take a look at this
   tool: [Material Design 3 Figma](https://www.figma.com/community/plugin/1034969338659738588/Material-Theme-Builder)

_Anyway, explaining every little detail here would be overkill and I perfectly understand that trying to learn all of
this
for a newbie can be **a lot**, so, If you need assistance on theming, feel free to open a
new [Discussion](https://github.com/palexdev/ENBManager/discussions) here on GitHub._

<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also
simply open an issue.
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- SUPPORT -->

## Support

If you like the project, and you want to show your support you can do it in various ways:

1) If you feel like it, you can pay me a coffee here [PayPal](https://www.paypal.com/paypalme/alxpar404/2)
2) Star the project here on GitHub
3) Endorse the mod on the [Nexus](https://www.nexusmods.com/skyrimspecialedition/mods/93764)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/palexdev/ENBManager.svg?style=for-the-badge

[contributors-url]: https://github.com/palexdev/ENBManager/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/palexdev/ENBManager.svg?style=for-the-badge

[forks-url]: https://github.com/palexdev/ENBManager/network/members

[stars-shield]: https://img.shields.io/github/stars/palexdev/ENBManager.svg?style=for-the-badge

[stars-url]: https://github.com/palexdev/ENBManager/stargazers

[issues-shield]: https://img.shields.io/github/issues/palexdev/ENBManager.svg?style=for-the-badge

[issues-url]: https://github.com/palexdev/ENBManager/issues

[product-screenshot]: images/main.png
