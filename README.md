<p align="center">
  <img src="https://raw.githubusercontent.com/microsoft/fluentui-emoji/main/assets/Books/3D/books_3d.png">
</p>

# BookBan

A simple way of preventing book bans. Will not allow players to pick up more items if there inventory is a specific size.

## Config

```yml
# Will log information about your current inventory size. Extra details in the
# server console
debug: false
# If the plugin is listening for events or not. Intended in case you need to
# disable the plugin without restarting the server
disabled: false

limit:
  # The maximum size that a players inventory is allowed to be
  inventory: 50674
  # The maximum size of item that the player is allowed to pick up. If this is
  # to large, the player will be able to pick up an item, but will crash when
  # they open their inventory
  item: 8260
  # This is the maximum size of books that are allowed to be created, intended
  # to stop the creation of books for bookbans in survival servers
  #
  # Note that the units for this are slightly different from the rest of the
  # available keys
  book: 8000
```

## Commands

- `/bookban reload` - Reloads the config file

## License

This project is a hard fork of [SeedMC's version](https://github.com/theseedmc/SeedBookban). Both the original and this one are under [MIT](https://github.com/trickypr/SeedBookban/blob/master/LICENSE).

### Logo

The package icon is from [Microsoft's Fluent Emoji](https://github.com/microsoft/fluentui-emoji). There is an [ongoing conversation](https://github.com/microsoft/fluentui-emoji/issues/18) regarding the license.

```
MIT License

Copyright (c) Microsoft Corporation.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```
