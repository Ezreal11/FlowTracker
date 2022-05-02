# -*- coding: utf-8 -*
import datetime
import json
import os
import math

def valid_quest(ProgrammerPath):
    if "interaction_traces" not in os.listdir(ProgrammerPath):
        print("Empty folders!")
        return


    scale_path=ProgrammerPath+"\\interaction_traces\\scale"
    folders= os.listdir(scale_path)


    refuse=0
    valid=0

    quests=[]
    for folder in folders:      #folder是文件夹名称
        dirpath=scale_path+"\\"+folder
        if os.path.isdir(dirpath):
            questionnaires=os.listdir(dirpath)
            for questionnaire in questionnaires:        #对每个问卷文件
                with open(dirpath+"\\"+questionnaire) as f:        #打开文件
                    lines=[]
                    while 1:
                        line=f.readline()
                        if not line:
                            break
                        lines.append(line.split(',',2))
                    if lines[len(lines)-2][1]=="submit":      #不是被拒绝的问卷
                        submit_time=int(lines[0][0])
                        engagement=-1
                        efficiency=-1
                        for line in lines:
                            if line[1]=="submit":
                                data=json.loads(line[2])
                                if data["title"]=="engagement":
                                    engagement=data["optionScore"]
                                elif data["title"]=="efficiency":
                                    efficiency=data["optionScore"]
                        quests.append({"subtime":submit_time,"engagement":engagement,"efficiency":efficiency})
                        valid+=1
                        #generate_piece({"subtime":submit_time,"engagement":engagement,"efficiency":efficiency})
                    else:
                        refuse+=1
                        #print(lines)
                        #print("######")
    #问卷信息写入文件
    with open(ProgrammerPath+"\\result.txt",'w') as newfile:
        for quest in quests:
            newfile.write(json.dumps(quest)+'\n')
    
    print("##########################")
    print(ProgrammerPath)
    print("valid:"+str(valid)+"          refuse:"+str(refuse))

#生成切片
def generate_pieces(ProgrammerPath):
    if "interaction_traces" not in os.listdir(ProgrammerPath):
        print("Empty folders!")
        return



    PiecePath=ProgrammerPath+"\\Pieces"
    with open(ProgrammerPath+"\\result.txt") as sc:
        scales=sc.readlines()
    pos=0
    current_scale=json.loads(scales[pos])
    piece_file=open(PiecePath+"\\"+str(pos)+".json",'w')
    piece_file.write(scales[pos])

    flag=False
    data_path = ProgrammerPath+"\\interaction_traces\\intellij" #数据文件夹目录
    folders= os.listdir(data_path)
    for folder in folders:      #folder是文件夹名称
        dirpath=data_path+"\\"+folder
        if os.path.isdir(dirpath):
            datafiles=os.listdir(dirpath)
            for datafile in datafiles:        #对每个数据文件
                if(pos>=len(scales)):
                    break
                with open(dirpath+"\\"+datafile) as f:        #打开文件
                    pieces=[]
                    lines=f.readlines()
                    for line in lines:
                        if line:
                            json_dict=json.loads(line)
                            datatime=datetime.datetime.fromtimestamp(json_dict["time"]/1000)
                            scaletime=datetime.datetime.fromtimestamp(current_scale["subtime"]/1000)
                            delta=scaletime-datatime
                            #print(delta.total_seconds())
                            if delta.total_seconds()<0:
                                piece_file.close()
                                pos+=1
                                while pos<len(scales) and len(scales[pos])<=1:
                                    pos+=1
                                #print(pos)
                                if pos>=len(scales):
                                    #print("end",len(scales))
                                    break
                                current_scale=json.loads(scales[pos])
                                scaletime=datetime.datetime.fromtimestamp(current_scale["subtime"]/1000)
                                delta=scaletime-datatime
                                piece_file=open(PiecePath+"\\"+str(pos)+".json",'w')
                                piece_file.write(scales[pos])
                            if delta.total_seconds()<=1800 and delta.total_seconds()>=0:
                                flag=True
                                piece_file.write(line)
                                pieces.append(json_dict)

#统计切片活动时间，是否忽略
def process(Piecename,Featurename):
    with open(Piecename) as f:   #检查每个切片
        worktime=0
        #line=f.readline()
        #quest=json.loads(line)
        line=f.readline()
        if not line:
            print("empty piece")
            pass
        else:
            lastop=json.loads(line)
            #totaltime=(datetime.datetime.fromtimestamp(quest["subtime"]/1000)-datetime.datetime.fromtimestamp(lastop["time"]/1000)).total_seconds()
            totaltime=1800
            while 1:
                line=f.readline()
                if not line:
                    break
                currentop=json.loads(line)
                lasttime=datetime.datetime.fromtimestamp(lastop["time"]/1000)
                currenttime=datetime.datetime.fromtimestamp(currentop["time"]/1000)
                delta=currenttime-lasttime
                
                #if delta.total_seconds()<120:
                #    worktime+=delta.total_seconds()
                #lastop=currentop
                delta=(currentop["time"]/1000-lastop["time"]/1000)
                if delta<120:
                    worktime+=delta
                lastop=currentop

            print("totaltime: ",totaltime,"  worktime: ",worktime)
            '''if totaltime!=0:
                print(worktime/totaltime)
            else:
                print("inf")
            if totaltime!=0 and worktime/totaltime>0.5:
                print("valid  piece    quest:",quest["subtime"])
            else:
                print("invalid  piece    quest:",quest["subtime"])
            print("#####################")'''
    with open(Featurename,'w') as feature_file:
        #feature_file.write(json.dumps(quest)+'\n')
        fv={}
        fv["worktime"]=worktime
        feature_file.write(json.dumps(fv))

#过滤不需要的数据
def abstract(Piecename,Abstractname):
    status=""       #当前状态
    with open(Piecename) as f:   #每个切片文件
        #line=f.readline()
        #quest=json.loads(line)      #读取问卷信息
        result_file=open(Abstractname,'w')     #向result_file中写入结果
        lastop=None
        while 1:
            line=f.readline()

            if not line:
                break
            else:
                currentop=json.loads(line)

                if currentop["type"]=="Key":
                    if currentop["when"]=="KEY_RELEASED":
                        lastkey=None
                        result_file.write(line)




                elif currentop["type"]=="Mouse":
                    if currentop["when"]=="MOUSE_CLICKED":     #点击次数相关，若拖动也算点击，使用released
                        result_file.write(line)
                    elif currentop["when"]=="MOUSE_DRAGGED":    #拖动
                        result_file.write(line)
                    elif currentop["when"]=="MOUSE_MOVED":      #移动
                        result_file.write(line)


                    elif currentop["when"]=="MOUSE_WHEEL":      #滚动
                        result_file.write(line)


                elif currentop["type"]=="Cursor":
                    result_file.write(line)

                elif currentop["type"]=="Window":
                    result_file.write(line)

                elif currentop["type"]=="Action":
                    result_file.write(line)

                elif currentop["type"]=="Execute":
                    result_file.write(line)

                elif currentop["type"]=="Debug":
                    result_file.write(line)

                elif currentop["type"]=="File":
                    result_file.write(line)

                elif currentop["type"]=="Project":
                    result_file.write(line)

                elif currentop["type"]=="App":
                    result_file.write(line)
            lastop=currentop
        result_file.close()
keytype=set()

#统计特征
def getfeature(Abstractname,Featurename):

    with open(Abstractname) as f:
        #统计量
        key_clicktimes=0
        key_clicktimes_bytype={}


        mouse_clicktimes=0
        mouse_dragged=0
        mouse_moved=0
        mouse_wheel=0
        mouse_distance=0
        debug=0

        maxcursor=0
        mincursor=2147483647
        total_cursor_change=0
        deactivated=0
        project_switch=0


        file_visit_length=0
        file_changed_length=0
        visited_files=set()
        changed_files=set()     #保存被更改的文件

        total_window_ops=0
        total_file_ops=0

        total_run=0

        navigate=0
        search=0
        find=0

        #辅助变量
        last_mouse_move=None



        lines=f.readlines()
        for line in lines:
            if not line:
                break
            currentop=json.loads(line)

            #mouse
            if currentop["type"]=="Mouse":
                if currentop["when"]=="MOUSE_CLICKED":
                    mouse_clicktimes+=1
                elif currentop["when"]=="MOUSE_DRAGGED":
                    mouse_dragged+=1
                elif currentop["when"]=="MOUSE_MOVED":
                    mouse_moved+=1

                    if last_mouse_move==None:
                        last_mouse_move=currentop
                    else:
                        lastpos=(last_mouse_move["data"]["coordinates"].split('/')[0]).replace('(','').replace(')','').split(',')
                        currentpos=(currentop["data"]["coordinates"].split('/')[0]).replace('(','').replace(')','').split(',')
                        mouse_distance+=math.sqrt(math.pow(int(currentpos[0])-int(lastpos[0]),2)+math.pow(int(currentpos[1])-int(lastpos[1]),2))
                        last_mouse_move=currentop
                        #如计算速度，可累加时间，若间隔时间过长，则忽略这段时间



                elif currentop["when"]=="MOUSE_WHEEL":
                    mouse_wheel+=1


            #keyboard
            elif currentop["type"]=="Key":
                if currentop["when"]=="KEY_RELEASED":
                    key_clicktimes+=1
                    keytype.add(currentop["data"]["text"])
                    if currentop["data"]["text"] not in key_clicktimes_bytype.keys():
                        key_clicktimes_bytype[currentop["data"]["text"]]=0
                    key_clicktimes_bytype[currentop["data"]["text"]]+=1


            elif currentop["type"]=="Cursor":
                if currentop["when"]=="cursor_changed":
                    newpos=currentop["data"]["newPos"].replace('(','').replace(')','').split(',')
                    mincursor=min(mincursor,int(newpos[0]))
                    maxcursor=max(maxcursor,int(newpos[0]))
                    oldpos=currentop["data"]["oldPos"].replace('(','').replace(')','').split(',')
                    mincursor=min(mincursor,int(oldpos[0]))
                    maxcursor=max(maxcursor,int(oldpos[0]))
                    total_cursor_change+=abs(int(newpos[0])-int(oldpos[0]))


            elif currentop["type"]=="Debug":
                debug+=1

            #切换
            elif currentop["type"]=="App":
                if currentop["when"]=="deactivated":
                    deactivated+=1

            elif currentop["type"]=="Project":
                #if currentop["when"]=="opened":
                project_switch+=1

            #文件
            elif currentop["type"]=="File":         #TODO:文件访问和修改的区分需要修改
                total_file_ops+=1
                #filef.write(line)
                if currentop["when"]=="opened" or currentop["when"]=="closed":
                    if "data" in currentop.keys():
                        if ("docLen" in currentop["data"].keys()) and (currentop["data"]["path"] not in visited_files):
                            file_visit_length+=int(currentop["data"]["docLen"])
                        visited_files.add(currentop["data"]["path"])

                elif currentop["when"]=="created" or currentop["when"]=="copied" or currentop["when"]=="content_changed":
                    visited_files.add(currentop["data"]["path"])
                    changed_files.add(currentop["data"]["path"])

                    #check! 复制文件长度计入
                    if currentop["when"]=="copied":
                        file_changed_length+=int(currentop["data"]["fileLen"])


                elif currentop["when"]=="changed":
                    if "newDocLen" in currentop["data"].keys() and "oldDocLen" in currentop["data"].keys():
                        file_changed_length+=abs(int(currentop["data"]["newDocLen"])-int(currentop["data"]["oldDocLen"]))
                    else:
                        print(line)

            elif currentop["type"]=="Window":
                total_window_ops+=1

            elif currentop["type"]=="Execute":
                if currentop["when"]=="started":
                    total_run+=1

            elif currentop["type"]=="Action":
                #filef.write(line)
                if "Navigate" in currentop["data"]["action"] or "navigate" in currentop["data"]["action"] or "navigation" in currentop["data"]["action"]: #or "Step" in currentop["data"]["action"]:
                    navigate+=1
                elif "id" in currentop["data"].keys() and currentop["data"]["id"]=="Find":
                    find+=1
                else:
                    pass
                    #filef.write(line)
                        


                    
                    
                    
                    
                    


        #将对应数据添加到特征向量中
        with open(Featurename,'r+') as feature_file:
            #quest=json.loads(feature_file.readline())
            tempfe=json.loads(feature_file.readline())
        with open(Featurename,'w') as feature_file:
            feature={}
            feature["worktime"]=tempfe["worktime"]
            feature["mouseclick"]=mouse_clicktimes
            feature["mousedragged"]=mouse_dragged
            feature["mousemoved"]=mouse_moved
            feature["mousewheel"]=mouse_wheel
            feature["mouse_move_distance"]=mouse_distance
            feature["keyclick"]=key_clicktimes
            feature["debug"]=debug
            if mincursor!=2147483647:
                feature["cursor_range"]=maxcursor-mincursor
            else:
                feature["cursor_range"]=0
            feature["distraction"]=total_cursor_change
            if maxcursor-mincursor!=0 and mincursor!=2147483647:
                feature["backtrace_rate"]=total_cursor_change/(maxcursor-mincursor)
            else:
                feature["backtrace_rate"]=0         #只访问了一行该怎么办
            feature["deactivated"]=deactivated
            feature["project_switch"]=project_switch

            feature["file_visited"]=len(visited_files)#file_opened
            feature["file_changed"]=len(changed_files)
            feature["file_visit_length"]=file_visit_length
            feature["file_changed_length"]=file_changed_length
            
            feature["fileops"]=total_file_ops
            feature["windowops"]=total_window_ops

            feature["navigate"]=navigate
            feature["find"]=find

            if "Letter" in key_clicktimes_bytype.keys():
                feature["Letter"]=key_clicktimes_bytype["Letter"]
            else:
                feature["Letter"]=0

            if "Number" in key_clicktimes_bytype.keys():
                feature["Number"]=key_clicktimes_bytype["Number"]
            else:
                feature["Number"]=0
            
            if "Punctuation" in key_clicktimes_bytype.keys():
                feature["Punctuation"]=key_clicktimes_bytype["Punctuation"]
            else:
                feature["Punctuation"]=0

            if "Enter" in key_clicktimes_bytype.keys():
                feature["Enter"]=key_clicktimes_bytype["Enter"]
            else:
                feature["Enter"]=0

            if "Delete" in key_clicktimes_bytype.keys():
                feature["Delete"]=key_clicktimes_bytype["Delete"]
            else:
                feature["Delete"]=0

            if "Backspace" in key_clicktimes_bytype.keys():
                feature["Backspace"]=key_clicktimes_bytype["Backspace"]
            else:
                feature["Backspace"]=0

            #feature["keytype"]=key_clicktimes_bytype

            #print(feature["backtrace_rate"])
            #feature_file.write(json.dumps(quest)+'\n')
            feature_file.write(json.dumps(feature))
            
def visit_Programmer(ProgrammerPath):
    print(ProgrammerPath)
    PiecePath=ProgrammerPath+"\\Pieces"
    FeaturePath=ProgrammerPath+"\\Features"
    AbstractPath=ProgrammerPath+"\\Abstract result"
    if not os.path.exists(PiecePath):
        os.mkdir(PiecePath)
    if not os.path.exists(FeaturePath):
        os.mkdir(FeaturePath)
    if not os.path.exists(AbstractPath):
        os.mkdir(AbstractPath)
    #valid_quest(ProgrammerPath)
    #generate_pieces(ProgrammerPath)
    pieces_process(ProgrammerPath)
    abstract(ProgrammerPath)
    getfeature(ProgrammerPath)
                        
def visit_all():
    DataPath="C:\\Users\\王伯涵\\Desktop\\Data"
    GroupNames= os.listdir(DataPath)
    for group in GroupNames:
        GroupPath=DataPath+"\\"+group
        Programmer_Names=os.listdir(GroupPath)
        for programmer in Programmer_Names:
            ProgrammerPath=GroupPath+"\\"+programmer    #每个人的路径
            visit_Programmer(ProgrammerPath)




def hello(a1,a2):
    print(a1)
    print(a2)




#valid_quest()
#generate_pieces()
#pieces_process("Pieces")
#abstract("Pieces")
#getfeature()
#print(keytype)