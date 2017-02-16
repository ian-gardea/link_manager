# link_manager

**Written and designed by:** Ian A. Gardea

---

**Note:**  Currently, this program is only designed for Windows operating systems.

---

**Description:** 

This program allows the dynamic creation, and execution
of links, and DOS commands. To begin, simply drag and drop links 
from your computer, to the main window to build your own, customized list of 
links. Once created, you can save your configuration to an XML file for future 
use on subsequent sessions.

By default, the file `configuration.xml` is saved in the same folder that the program is located in.

---

**Features:**

* Create links by using drag and drop.
 * Folders are also accepted, and will copy ALL files contained within.

* Manually add links from the `File` menu. Separators can also be added.

* Manually create a link by pasting in the target of the link on the currently selected tab using `CTRL + V`.
 * This will prompt to add a name for the link, showing the pasted target so the user can verify accurracy prior to insertion.

* Drag and drop to arrange the preferred order of any link or tab.

* Lock/Unlock the screen to disable accidental editing.

* Double left-click on any link to rename it.

* Right-click on any link to assign it a new target.

* Save the configuration to a local XML file, `configuration.xml`.
 * This file will retain any changes made to the list. This file can be replaced with any configuration.xml file that was made by this    program.

**Tip:** You can define your own customized variable if you use CUSTOM_VAR (all caps) in any link definition.
	Simply set the variable value from the file menu. This can be handy for definitions that constantly
	need to change.

**Tip:** If a URL's target starts with "command: " (without quotes, and not case sensitive),
        then it will treat the following text as a DOS command.
   **Ex:**  A link having the target "command: echo hello" will display a DOS window with the
        output text "hello".

---

**Available Keyboard Shortcut Commands:**

* `Alt  + F` will display File menu.
* `Alt  + H` will display Help menu.
* `Ctrl + D` will delete the current tab.
* `Ctrl + L` will enable/disable editing of the program.
* `Ctrl + N` will empty the current configuration, and make a new one.
* `Ctrl + R` will revert all changes made to the document made since the last save.
* `Ctrl + S` will save the changes made to the configuration document.
* `Ctrl + T` will prompt to add a new tab.
* `Ctrl + V` will add a new link to the current tab, prompting for the name. The data pasted is assumed to be the target.
* `n` will add prompt to add a new link.
* `r` will run the selected links on the current tab.
* `s` will add a new link separator.
* `v` will prompt to assign a customized variable.
* `delete` will delete selected links on the current tab.

########################################################################################
