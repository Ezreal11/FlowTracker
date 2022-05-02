import json
import os
import numpy as np
from matplotlib import pyplot as plot
import scipy
from sklearn import metrics
from sklearn.model_selection import cross_val_score, train_test_split
from sklearn.preprocessing import MinMaxScaler, StandardScaler
from GCForest import gcForest


X=[]
Y=[]
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
                if feature["worktime"]<900:     #忽略工作时间少于一般的特征，准确率明显提升
                    continue
                for name,value in feature.items():
                    vector.append(value)
                
                if lable["engagement"]<3:
                    X.append(vector)
                    Y.append(0)
                if lable["engagement"]>3:
                    X.append(vector)
                    Y.append(1)
                if lable["engagement"]==3:
                    count+=1
                #print(len(vector))

#ss = StandardScaler()
ss = MinMaxScaler()
stdX=ss.fit_transform(X)
print(len(X))

def fit_and_score():    
    rfc=gcForest(shape_1X=(1,3),window=[2])
    Xtrain,Xtest,Ytrain,Ytest=train_test_split(stdX,Y,test_size=0.1)
    #rfc.fit(Xtrain,Ytrain)
    Y_pred=rfc.cascade_forest(stdX,Y)
    
    print(metrics.confusion_matrix(Ytest,Y_pred))
    #print(rfc.score(Xtest,Ytest))

def cross_score():
    rfc_l=[]
    for i in range(10):
        rfc=gcForest(shape_1X=(1,3),window=[2])
        rfc_s=cross_val_score(rfc,stdX,Y,cv=4).mean()
        rfc_l.append(rfc_s)
    plot.plot(range(1,11),rfc_l,label="random forest")
    plot.legend()
    plot.show()

def estimator_curve():
    supera=[]
    for i in range(200):
        rfc=gcForest()
        rfc_s=cross_val_score(rfc,stdX,Y,cv=10).mean()
        supera.append(rfc_s)
    print(max(supera),supera.index(max(supera)))
    #plot.figure(figsize=[20,5])
    plot.plot(range(1,201),supera)
    plot.show()

#cross_score()
fit_and_score()
#estimator_curve()