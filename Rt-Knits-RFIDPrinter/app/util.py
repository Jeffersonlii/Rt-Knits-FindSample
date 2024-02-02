
import os
import sys

# determins if the app is running as an EXE
# https://stackoverflow.com/questions/404744/determining-application-path-in-a-python-exe-generated-by-pyinstaller
def isRunningAsExecutable():
    return getattr(sys, 'frozen', False)

# gets the executing path of the application
# this function is used for crafting file paths
# note that this path is different depending on if the app is executed using app.py or as an .EXE
app_path = None
def getAppPath():
    global app_path
    if(app_path is not None):
        return app_path

    if isRunningAsExecutable():
        # if this app is ran as a executable (compiled by pyinstaller)
        app_path = os.path.dirname(sys.executable)
    else:
        # if this app is ran on command line (python app.py)
        app_path = os.path.dirname(os.path.abspath(__file__))
    return app_path

# replace occurences of old with new in s starting from the back 
# ex rreplace("hellolo,"l", "s", 1) => "helloso"
def rreplace(s, old, new, occurrence):
    li = s.rsplit(old, occurrence)
    return new.join(li)