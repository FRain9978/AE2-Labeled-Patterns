---
navigation:
  title: Labeled Pattern Access Terminal
  icon: ae2labeledpatterns:labeled_pattern_access_terminal
  parent: ae2lps_intro.md
item_ids:
  - ae2labeledpatterns:labeled_pattern_access_terminal
  - ae2labeledpatterns:wireless_labeled_pattern_access_terminal
---

# Labeled Pattern Access Terminal
<ItemImage id="ae2labeledpatterns:labeled_pattern_access_terminal" scale="4"></ItemImage>
The Labeled Pattern Access Terminal is a new terminal that can display labeled patterns separately according to labels.Obviously, it has all basic functions of the normal ME Pattern Access Terminal.

## GUI
There 3 new buttons in the terminal GUI compared to the normal version:
- **Label Filter**: Default is empty, click to index all labels the grid has found in pattern providers. The screen will refresh to show the matched pattern providers.
- **Show Group Selection**: Default is off, click to decide whether to show a checkbox ahead each group header. When enabled, you can select any of them to mark this group. This will be useful to decide which group to be the quick move target.
- **Quick Move Convenience**: Default is NONE, which means no additional action when you shift click on a pattern provider.
  - NONE: Shift click an encoded pattern in inventory will quickly move to a provider in the row that has an empty slot.When there is no selected group, it will scan all visible rows in screen, otherwise it will scan current selected group. The move-priority is also depending on the mouse button used to click:
    - Left click: Move to the first empty slot inner current selected group(if it has) or in all visible rows.
    - Right click: Move to the last empty slot inner current selected group(if it has) or in all visible rows.  
  - ONCE_FOR_ALL: In addition to NONE, in this mode shift click action will try to duplicate the pattern many times to insert them to every pattern provider respective in current selected group. It will not work if there is no selected group. This will be helpful if you need to set up the same manufactures with pattern providers more than one times. You can insert the pattern at the same time. Notice that it needs you to have blank patterns in your inventory. If you don't have enough blank patterns, it will stop inserting when it runs out of all blank patterns.
  - ONCE_FOR_ALL_STRICT: In addition to ONCE_FOR_ALL, in this mode it will not do anything if the number of blank patterns is less than the number of pattern providers in current selected group.