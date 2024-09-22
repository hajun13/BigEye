from socket import *
from select import select
from openai import OpenAI
import time
from tqdm  import tqdm
from _socket import AF_INET, socket, SOCK_STREAM
import socket
import os
import openai
from sklearn.feature_extraction.text import TfidfVectorizer
from konlpy.tag import Okt



OPENAI_API_KEY = os.environ.get("API_KEY")

openai.api_key = OPENAI_API_KEY

client = OpenAI(api_key=OPENAI_API_KEY)

# 파이썬이랑 자바가 데이터를 주고받는 메인 메소드
def sendmessage():
    HOST = '127.0.0.1'
    PORT = 9999
    BUFSIZE = 4096
    num = 1

    try:
        print("서버 시작 ...")
        ## 
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        
            s.bind((HOST, PORT))
            s.listen()

            while (True):
                print("\n 클라이언트로부터 데이터 입력 대기중 ... ") 
                client_socket, client_address = s.accept()
                   

                result : str = " "
                
                try:
                    cs = client_socket.recv(BUFSIZE).decode('UTF-8', 'ignore')
                    cs = int(cs)
                    print(cs)
                except ValueError:
                    print("pass")
                    continue
                
                i = 0
                for i in range(cs): #ex) 5문장씩 10번이면 cs == 10
                    print(i)
                    recva = client_socket.recv(BUFSIZE).decode('UTF-8', 'ignore')
                    print(recva)
                    if(recva == None):
                        i -= 1

                    if(i == cs/2):
                        result += recva.replace("\n", "")
                        result += "\n"
                        client_socket.sendall(bytes("more\n", 'UTF-8'))
                        continue

                    result += recva.replace("\n", "")
                    client_socket.sendall(bytes("more\n", 'UTF-8'))
                    
                print("데이터 수신완료")
                n_rlist = result.split("\n")
                nrsize = str(len(n_rlist))
                print("n_rlist size : " +nrsize)
                        
                def summary():                    

                    start_pno = 0
                    summary_list = [{
                        'role': 'system',
                        'content': 'summary request prompt ... '



                    }]
                    summary_list2 = [ ]

                    count = 0
                    content = ''

            
                    for pno in tqdm(range(start_pno, len(n_rlist) + 1)):
                        

                        if count == len(n_rlist): #or pno == len(rlist)-1:

                            messages = [{
                                'role': 'system',
                                'content': 'summary request prompt ... '

                            }, {
                                'role': 'user',
                                'content': f'Summarize this: {content}'
                            }]


                            res = client.chat.completions.create(
                                model = 'gpt-4o-mini',
                                messages=messages
                            )
                                
                            # msg = res['choices'][0]['message']['content'] 
                            msg = res.choices[0].message.content

                            summary_list.append({
                                'role': 'user',
                                'content': msg
                            })

                            summary_list2.append(msg)


                            #1
                            count = 0
                            content = ''

                            # qwe = res.

                        else:
                            content += n_rlist[pno] + ' '
                            count += 1

                    return summary_list2

                summary_list = summary()
                print("최종 요약 ...\n\n ")
                final_result : str = " "
                for i in range(len(summary_list)) :
                    print(summary_list[i])
                    final_result += summary_list[i]

                if(recva == None) :
                    continue

                final_result = final_result.replace("\n", " ")
                final_result = str(final_result)

                
                final_result = final_result.replace("\n", " ").replace("```html", "").replace("```", "")
                final_result += "\n"

                client_socket.sendall(bytes(final_result, 'UTF-8'))
                print("전송 완료")

                # 키워드 추출
                okt = Okt()
                korean_stop_words = [
                    '이', '그', '저', '것', '수', '들', '등', '및', '의', '를', '에', '가', '은', '는', '있', '하', '되', '이지', '있다', '하다', '으로', '하다',
                    '과', '와', '도', '로', '에서', '까지', '보다', '그리고', '그러나', '그래서', '또한', '하지만',
                    '이다', '같다', '말하다', '되다', '자다', '않다'
                ]
            
                documents = [result]

                tokenized_docs = [' '.join([word for word in okt.nouns(doc) if word not in korean_stop_words]) for doc in documents]

                vectorizer = TfidfVectorizer()

                tfidf_matrix = vectorizer.fit_transform(tokenized_docs)               

                feature_names = vectorizer.get_feature_names_out()
                tfidf_scores = [(feature_names[word_idx], tfidf_matrix[0, word_idx]) for word_idx in tfidf_matrix.nonzero()[1]]

                # TF-IDF 점수를 기준으로 내림차순 정렬
                sorted_tfidf_scores = sorted(tfidf_scores, key=lambda x: x[1], reverse=True)

                inx = 0
                for word, score in sorted_tfidf_scores:
                    if(inx == 100) :
                        break

                    message = f"{word}\t{score:.4f}\n"
                    client_socket.sendall(message.encode('UTF-8'))  
       
                    print(f"{word}: {score:.4f}")
                    inx += 1

                client_socket.sendall("END\n".encode('UTF-8'))
                num+=1

                client_socket.close()
            


    except Exception as e:
        client_socket.close()
        print(e)


sendmessage()

#region 설명 섹션

#endregion
