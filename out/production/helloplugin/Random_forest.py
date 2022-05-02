# -*- coding: utf-8 -*
from importlib.resources import path
import json
from logging import root
import os
from pyexpat import model
import sys
from matplotlib import pyplot as plot
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import cross_val_score, train_test_split
from sklearn.preprocessing import MinMaxScaler, StandardScaler
from sklearn import metrics
import joblib

X=[]
Y=[]
stdX=[]
zerocount=0
def generate_vector(feature):
    X=[]
    X.append(feature["worktime"])
    X.append(feature["mouseclick"])
    X.append(feature["mousedragged"])
    X.append(feature["mousemoved"])
    X.append(feature["mousewheel"])
    X.append(feature["mouse_move_distance"])
    X.append(feature["keyclick"])
    X.append(feature["debug"])
    X.append(feature["cursor_range"])
    X.append(feature["distraction"])
    X.append(feature["backtrace_rate"])
    X.append(feature["deactivated"])
    X.append(feature["project_switch"])
    X.append(feature["file_visited"])
    X.append(feature["file_changed"])
    X.append(feature["file_visit_length"])
    X.append(feature["file_changed_length"])
    X.append(feature["fileops"])
    X.append(feature["windowops"])
    X.append(feature["navigate"])
    X.append(feature["find"])
    X.append(feature["Letter"])
    X.append(feature["Number"])
    X.append(feature["Punctuation"])
    X.append(feature["Enter"])
    X.append(feature["Delete"])
    X.append(feature["Backspace"])
    return X

def loading():
    count=0
    DataPath="C:\\Users\\王伯涵\\Desktop\\Data"
    GroupNames= os.listdir(DataPath)
    for group in GroupNames:
        GroupPath=DataPath+"\\"+group
        Programmer_Names=os.listdir(GroupPath)
        for programmer in Programmer_Names:
            ProgrammerPath=GroupPath+"\\"+programmer    #每个人的路径
            FeaturePath=ProgrammerPath+"\\Features"
            feature_names=os.listdir(FeaturePath)
            for feature_name in feature_names:
                with open(FeaturePath+"\\"+feature_name) as f:
                    vector=[]
                    lable=json.loads(f.readline())
                    feature=json.loads(f.readline())
                    if feature["worktime"]<900:     #忽略工作时间少于一般的特征，准确率明显提升、小于3的更少了。。。
                        continue
                    #for name,value in feature.items():
                    #    vector.append(value)

                    vector=generate_vector(feature)
                    if lable["engagement"]<3:
                        X.append(vector)
                        Y.append(0)
                        count+=1
                    if lable["engagement"]>3:
                        X.append(vector)
                        Y.append(1)
                    
                    '''allfeatures.write(json.dumps(feature)+'\n')
                    allfeatures.write(json.dumps(lable)+'\n')
                    allfeatures.write('\n')
                    allfeatures.write('\n')
                    #print(len(vector))'''
    ss = MinMaxScaler()
    global stdX
    stdX=ss.fit_transform(X)
    print(count)
    print(len(stdX))


def recognize(featurefile):
    print("in recognize func")
    with open(featurefile) as f:
        #f.readline()
        feature=json.loads(f.readline())
    print(feature)
    maxX=[1.80000000e+03,6.04000000e+02,8.83200000e+03,4.56760000e+04,
          1.82620000e+04 ,5.23982779e+05, 1.59000000e+03, 1.04500000e+03,
          2.87960000e+04, 1.20329400e+06, 1.05127905e+02, 1.18000000e+02,
          5.00000000e+00, 1.99270000e+04, 1.99260000e+04 ,4.86673300e+06,
          3.57755012e+08, 5.67820000e+04, 1.38500000e+03, 5.90000000e+02,
          2.30000000e+01, 8.12000000e+02, 8.50000000e+01, 1.35000000e+02,
          1.88000000e+02, 8.20000000e+01, 3.61000000e+02]

    X=[]
    vector=generate_vector(feature)
    #print(vector)
    
    vector=[vector[i]/maxX[i] for i in range(len(vector))]  #标准化
    
    X.append(vector)
    
    paths=featurefile.split('/')
    print(paths)
    rootpath=paths[0:-1]
    modelpath=""
    for s in rootpath:
        modelpath+=s+"/"
    modelpath+="train_model.m"
    print(modelpath)
    rfc=joblib.load(modelpath)
    print("after load")
    print(X[0])
    Y_pred=rfc.predict(X)
    print(Y_pred[0])

    #Y_pred[0]=0
    if Y_pred[0]==1:
        sys.exit(101)
    elif Y_pred[0]==0:
        sys.exit(100)
    #return Y_pred[0]




#loading()
#fit_and_score()
#print(len(X))
#cross_score()
#train()
#recognize()

recognize(sys.argv[1])
print(222)
    
#max_depth_curve()
#max_features_curve()
#estimator_curve()