# NDS_ROM_Editor

This is a Nintendo DS ROM editor that tries to be modern and to gather all important things for ROM exploration and edition. We want this tool to be a complete tool for ROM hacking and to be portable. That's why it's made in Java, because everyone has Java thanks to Minecraft.

## Installation

Either download the JAR file in releases (if any) or build the repo. We are simple people, we don't want to use *main stream* libraries that can contain malwares (hi Log4J) and so we decided to not use Maven (nor Gradle). If we need a lib, we want it to be either user installed or very clean (for Arm disassembly maybe we will change our mind...). 

## How to use it? 

Just open the JAR and tada! The tool is free and wants to be as complete as possible. This means that we want it possible to edit palettes, tiles, screens, animations, and even 3D! Everything is WIP of course but we will try our best. 

## Motivations

We want to create a Pokemon Platinum ROM hack. PPRE does not launch because Python2 is dead and PyQt4 is does not want to compile (and the repo is a mess, you don't understand how anything works). Tinke works well but is not intuitive at all and sometimes crashes. So I wondered: why not making my tool during my free time? And here we are

## Technical references

The documentation is available in [https://typst.app/project/rYM1Ill6REiC0oUVu0nlk6](https://typst.app/project/rYM1Ill6REiC0oUVu0nlk6)

## Acknowledgments

This is inspired from PPRE repo (or at least what can be understood from it) and from various sources from the internet:

- PPRE: [https://github.com/projectpokemon/PPRE](https://github.com/projectpokemon/PPRE)
- Tinke: [https://github.com/pleonex/tinke](https://github.com/pleonex/tinke)
- Documentation [https://problemkaputt.de/gbatek-nds-reference.htm](https://problemkaputt.de/gbatek-nds-reference.htm) and [https://loveemu.hatenablog.com/entry/20091002/nds_formats](https://loveemu.hatenablog.com/entry/20091002/nds_formats)