import sys
import os
import requests
from requests.auth import HTTPBasicAuth
import re
directoryPath=sys.argv[1]
type=sys.argv[2]
logFilePath=sys.argv[3]
def generateParametersList(typeName) :
	with open(logFilePath,"w") as logFileObject :
		try :
			parameterList=[]
			typeList=[typeName]
			i=0
			while True :
				if i==len(typeList) :
					print("breaking")
					break
				else :
					#print("type is " + typeList[i])
					url='http://localhost:21000/api/atlas/types/'+typeList[i]
					#print(url)
					response=requests.get(url,auth=HTTPBasicAuth('admin', 'admin'))
					pattern1=re.compile("superTypes[^\]]+\]")
					iterator1=pattern1.finditer(str(response.json()))
					s1=str(response.json())
					#print(s1)
					for match in iterator1 :
						#print("hii")
						start,end=match.span()
						#print(str(start) + "-" + str(end))
						s2=s1[start+14:end-1]
						#print(s2)
						if s2!="" :
							for st in s2.split(",") :
								typeList.append(st.strip()[1:-1])
								print("super type is " + st.strip()[1:-1])
					#print("hii")
					pattern2=re.compile("attributeDefinitions.+")
					iterator2=pattern2.finditer(s1)
					for match in iterator2:
						start,end=match.span()
						s3=s1[start:end]
						#print(s3)
						pattern3=re.compile("\{[^\}]+\}")
						iterator3=pattern3.finditer(s3)
						for match in iterator3 :
							start,end=match.span()
							s4=s3[start+1:end-1]
							#print(s4)
							parameter=s4.split(",")[0].split(":")[1].strip()[1:-1]
							multiplicity=s4.split(",")[2].split(":")[1].strip()[1:-1]
							parameterList.append(parameter + "=" + multiplicity)
							print("parameter is " + parameter + " and multiplicity is " + multiplicity)
					#print("i is " + i) 
					i=i+1
			return parameterList
		except Exception as e :
			print(e)
			logFileObject.write(str(e))

def generateInput(directoryPath,parameterList):
	with open(logFilePath) as logFileObject :
		try :
			if os.path.exists(directoryPath+"/"+"input")==False :
				os.mkdir(directoryPath+"/"+"input")
			with open(directoryPath+"/"+"input"+"/"+"parameters.txt","w") as fileObject:
				#fileObject.write("directoryPath="+directoryPath+"\n")
				for parameter in parameterList :
					fileObject.write(parameter+"\n")
		except Exception as e :
			logFileObject.write(str(e))

parameterList=generateParametersList(type)
print(parameterList)
generateInput(directoryPath,parameterList)
