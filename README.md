# Prismus

*A software tool for playing, writing, and analyzing tonal music using relative intonation.*

# Overall Design Choices

### Framework: JUCE

*Audio Plug-In, uses C++ for audio processing, interfacing, GUI, etc.*

**Why not Python?**

Too slow for audio processing.

**Why not use Python for GUI?**

JUCE already is a robust system for rendering music-related GUI elements. More complex rendering can be done with OpenGL.

### IDE: Visual Studio Community 2022

*Recommended IDE for JUCE development in Windows 11*

# GUI DESIGN

### Graph

*Visual sheet music space for rendering musical instructions within a relative intonation structure.*

Resizable ONLY SCALED both horizontally and vertically, not either-or.

<span style="color: blue;">**???**</span> Allow for independence of beat and time from horizontal visual space, as in classical 
sheet music? Or choose a time-based horizontal scale and force that? Or allow for certain sections of music to each have 
their own time-based horizontal scale and force consistency within the section?

### Note