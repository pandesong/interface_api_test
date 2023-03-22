import shlex
import subprocess
import md5sum


def upload_file(ip,username,poolname,password,local,remote):
    uploadfile_cmd="smbclient     -s   e:\smbclient\smb.conf  //%s/%s_%s    -U%s  %s" % (ip,username,poolname,username,password)
    ex = subprocess.Popen(uploadfile_cmd, stdout=subprocess.PIPE,stdin=subprocess.PIPE,shell=False)
    ex.stdin.write('put  %s   %s'  % (local,remote))
    ex.stdin.close()
    data=ex.stdout.read()
    ex.stdout.close()
    return data


if __name__ == '__main__':
    import sys
    print len(sys.argv)
    ip=sys.argv[3]
    username=sys.argv[1]
    poolname=sys.argv[2]
    password=sys.argv[4]
    local=sys.argv[5]
    local=local.replace(":","");
    local=local.replace("\\","/")
    local="/cygdrive/%s" % local
    print local
    remote=local.split("/").pop()
    print remote
    print upload_file(ip,username,poolname,password,local,remote)
