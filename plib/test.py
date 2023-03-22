def giftojpg(gif):
    from PIL import Image
    gif_file = gif
    img = Image.open(gif_file)
    print(img.mode, img.format)
    img=img.convert('RGB')
    print(img.mode, img.format)
    img.save('testcase\\code.jpg')


def adb_shell(cmd):
    import subprocess
    res = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    result = res.stdout.read()
    res.wait()
    res.stdout.close()
    return result



def getCodejpg(value):
    import base64
    import os
    os.chdir("plib")
    try:
        os.remove("code.jpg")
    except:
        pass
    fa=open("code.jpg","wb")
    fa.write(base64.b64decode(value))
    fa.close()
    code=adb_shell("codetest.exe     code.jpg")
    print str(code.split("Loaded.")[1])


def gettimestamp():
    import time
    return  int(time.time())/1000
import sys
if __name__ == '__main__':
    #print(sys.argv[0])
    getCodejpg(sys.argv[1])