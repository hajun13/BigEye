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
    PORT = 80
    BUFSIZE = 4096
    num = 1

    try:
        ## 
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        
            s.connect((HOST, PORT))
            s.sendall(bytes("test", 'UTF-8'))
            while (True):
                print("\n 서버로부터 데이터 입력 대기중 ... ")    


                result : str = " "
                        
                cs = s.recv(BUFSIZE).decode('UTF-8', 'ignore')
                cs = int(cs)
                print(cs)
                
                index = 0
                i = 0
                for i in range(cs): #ex) 5문장씩 10번이면 cs == 10
                    print(i)
                    index += 1
                    recva = s.recv(BUFSIZE)
                    if(i == cs/2):
                        print("+_+_+_+_+__+_+_+_+_+_+_+_+-=-=-=-=-+_+_")
                        print(cs)
                        print("+_+_+_+_+__+_+_+_+_+_+_+_+-=-=-=-=-+_+_")
                        result += recva.decode('UTF-8', 'ignore').replace("\n", "")
                        result += "\n"
                        s.sendall(bytes("more\n", 'UTF-8'))
                        continue
                    result += recva.decode('UTF-8', 'ignore').replace("\n", "")
                    s.sendall(bytes("more\n", 'UTF-8'))
                    

                print("데이터 수신완료")
                print("+++++++++++++++++++++++++++++++++++++++++++++++++")
                print(result)
                print("+++++++++++++++++++++++++++++++++++++++++++++++++")
        
                n_rlist = result.split("\n")
                nrsize = str(len(n_rlist))
                print("n_rlist size : " +nrsize)
                        
                def summary():                    

                    start_pno = 0
                    summary_list = [{
                        'role': 'system',
                        'content': 'This assistant organizes only important sentences and answers in Korean. Whenever you see \'()\' in between, the content that follows is taken from a different address. Please combine all the contents separated by \'()\' and extract and summarize only the important contents. The summary can be summarized in five or more lines. Also, please emphasize important words by changing the font size and making the letters bold. After summarizing, please use \'-\' or \'•\' to mark paragraphs. Finally, wrap everything you request from now on into a single <div> tag. Of all the sentences you received, write down the positive or negative sentences or sentences and comments. Also, write down what percentage out of 100 points are positive responses and what percentage out of 100 points are negative responses and put them in the only one <span> tag. (Like <span>positive response: 60%<br>negative response: 40%</span> this)The summarized response must contain HTML tags appropriate for responses sent as HTML.'


                    }]
                    summary_list2 = [ ]

                    count = 0
                    content = ''

                    total_steps = len(n_rlist)
            
                    for pno in tqdm(range(start_pno, len(n_rlist) + 1)):
                        

                        if count == len(n_rlist): #or pno == len(rlist)-1:

                            messages = [{
                                'role': 'system',
                                'content': 'This assistant organizes only important sentences and answers in Korean. Whenever you see \'()\' in between, the content that follows is taken from a different address. Please combine all the contents separated by \'()\' and extract and summarize only the important contents. The summary can be summarized in five or more lines. Also, please emphasize important words by changing the font size and making the letters bold. After summarizing, please use \'-\' or \'•\' to mark paragraphs. Finally, wrap everything you request from now on into a single <div> tag. Of all the sentences you received, write down the positive or negative sentences or sentences and comments. Also, write down what percentage out of 100 points are positive responses and what percentage out of 100 points are negative responses and put them in the only one <span> tag. (Like <span>positive response: 60%<br>negative response: 40%</span> this)The summarized response must contain HTML tags appropriate for responses sent as HTML.'
                            }, {
                                'role': 'user',
                                'content': f'Summarize this: {content}'
                            }]

                            print(":::::::::::::::::::::::::::::::::::::::::::::\n" + content)

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
                            
                        time.sleep(1)

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

                s.sendall(bytes(final_result, 'UTF-8'))
                print("전송 완료")

                print("+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_")
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
                    s.sendall(message.encode('UTF-8'))  
       
                    print(f"{word}: {score:.4f}")
                    inx += 1

                s.sendall("END\n".encode('UTF-8'))
                num+=1
            


    except Exception as e:
        s.close()
        print(e)


sendmessage()

#region 설명 섹션
# 이 섹션은 접힐 수 있습니다.
# 중요한 설명을 여기에 추가하세요.
#endregion
