from News_Classify import Classify
from Wordcloud_Image import Get_Wordcloud
from Get_Summary import Get_summary
from Get_URL import Get_url
import requests
from flask import Flask, request, jsonify
import os

app = Flask(__name__)


@app.route('/article/getclassification', methods=["POST"])  # 第一个接口，用于新闻标题分类
def first_post():
    my_json = request.get_json()  # 获取后端传入的json形式的数据
    get_title = my_json.get("title")  # 提取出title
    print('新闻标题:', get_title)
    classify = Classify()  # 类实例化
    result = classify.classify('{0}'.format(get_title))

    print('分类结果为:', result[0])
    return str(result[0])


@app.route('/article/processContent', methods=["POST"])  # 第二个接口，用于生成词云图
def second_post():
    my_json = request.get_json()  # 获取后端传入的json形式的数据
    get_url = my_json.get("url")  # 提取出url
    get_articleId = my_json.get('articleId')  # 用于下面拼接url
    get_posterId = my_json.get('posterId')  # 用于下面拼接url
    print(get_url, 'has been got')
    wordcloud = Get_Wordcloud()  # 类实例化
    response = requests.get(url=get_url)  # 访问url
    response.encoding = 'utf-8'  # 设置文本编码模式为utf-8
    page_text = response.text  # 提取文本内容
    url_getter = Get_url()  # 类实例化
    url_ = url_getter.get_url(page_text)  # 获取图片的url
    w, word = wordcloud.get_wordcloud(page_text)  # 生成词云图,并获取标题的前两个词
    # 创建动态路径文件
    if os.path.isdir('D://Data/WordCloud/{0}'.format(get_posterId)):
        pass
    else:
        os.makedirs('D://Data/WordCloud/{0}'.format(get_posterId))
    # 保存图片
    w.to_file('D://Data/WordCloud/{0}/{1}.png'.format(get_posterId, get_articleId))

    # 证明已经完成图片的保存
    print('WordCloud from {0} has been saved'.format(get_url))

    getsummary = Get_summary()  # 类实例化
    summary = getsummary.get_summary(page_text)  # 获取摘要

    print('\n')
    print('abstract :', summary)
    print('word :', word)
    print('firstImg :', url_)
    return jsonify({'abstract': summary, 'word': word, 'firstImg': url_})


# title abstract img word posttime categray body
@app.route('/article/getnews', methods=['POST'])  # 第三个接口，用于新闻的更新
def third_post():
    my_json = request.get_json()
    get_number = my_json.get('number')
    print('number has been got : ' + str(get_number))
    data = []
    all_content = os.listdir(r'D:\Data\article\body')
    if get_number <= len(all_content):
        count = get_number
    else:
        count = len(all_content)
    for i in range(count):
        with open(r'D:\Data\article\title\{0}'.format(i+1), 'r', encoding='utf-8') as f:
            title = f.read()
        if os.path.exists(r'D:\Data\article\title\{0}'.format(i+1)):
            os.remove(r'D:\Data\article\title\{0}'.format(i+1))

        with open(r'D:\Data\article\summary\{0}'.format(i+1), 'r', encoding='utf-8') as f:
            abstract = f.read()
        if os.path.exists(r'D:\Data\article\summary\{0}'.format(i + 1)):
            os.remove(r'D:\Data\article\summary\{0}'.format(i + 1))

        with open(r'D:\Data\article\img\{0}'.format(i+1), 'r', encoding='utf-8') as f:
            img = f.read()
        if os.path.exists(r'D:\Data\article\img\{0}'.format(i + 1)):
            os.remove(r'D:\Data\article\img\{0}'.format(i + 1))

        with open(r'D:\Data\article\freq\{0}'.format(i+1), 'r', encoding='utf-8') as f:
            word = f.read()
        if os.path.exists(r'D:\Data\article\freq\{0}'.format(i + 1)):
            os.remove(r'D:\Data\article\freq\{0}'.format(i + 1))

        with open(r'D:\Data\article\time\{0}'.format(i+1), 'r', encoding='utf-8') as f:
            posttime = f.read() + ' 00:00:00'
        if os.path.exists(r'D:\Data\article\time\{0}'.format(i + 1)):
            os.remove(r'D:\Data\article\time\{0}'.format(i + 1))

        with open(r'D:\Data\article\category\{0}'.format(i+1), 'r', encoding='utf-8') as f:
            category = f.read()
        if os.path.exists(r'D:\Data\article\category\{0}'.format(i + 1)):
            os.remove(r'D:\Data\article\category\{0}'.format(i + 1))

        with open(r'D:\Data\article\body\{0}'.format(i+1), 'r', encoding='utf-8') as f:
            body = f.read()
        if os.path.exists(r'D:\Data\article\body\{0}'.format(i + 1)):
            os.remove(r'D:\Data\article\body\{0}'.format(i + 1))

        if len(body) > 20:
            data.append({'title': title, 'abstract': abstract,
                         'img': img, 'word': word, 'postTime': posttime,
                         'categoryId': category, 'body': body})
        else:
            pass
    print('data has been sent')
    return jsonify(data)


@app.route('/article/processWordCloud', methods=['POST'])
def forth_post():
    my_json = request.get_json()  # 获取后端传入的json形式的数据
    get_url = my_json.get("url")  # 提取出url
    get_articleId = my_json.get('articleId')  # 用于下面拼接url
    get_posterId = my_json.get('posterId')  # 用于下面拼接url
    print(get_url, 'has been got')
    wordcloud = Get_Wordcloud()  # 类实例化
    response = requests.get(url=get_url)  # 访问url
    response.encoding = 'utf-8'  # 设置文本编码模式为utf-8
    page_text = response.text  # 提取文本内容
    w, word = wordcloud.get_wordcloud(page_text)  # 生成词云图,并获取标题的前两个词
    # 创建动态路径文件
    if os.path.isdir('D://Data/WordCloud/{0}'.format(get_posterId)):
        pass
    else:
        os.makedirs('D://Data/WordCloud/{0}'.format(get_posterId))
    # 保存图片
    w.to_file('D://Data/WordCloud/{0}/{1}.png'.format(get_posterId, get_articleId))

    # 证明已经完成图片的保存
    print('WordCloud from {0} has been saved'.format(get_url))
    return jsonify('over')


@app.route('/comment/processWordCloud', methods=['POST'])
def fifth_post():
    my_json = request.get_json()
    get_comment = my_json.get('comment')
    get_id = my_json.get('id')
    comment = ''
    for i in get_comment[0:-1]:
        comment = comment + ' {0}'.format(i)
    number = get_id
    wordcloud = Get_Wordcloud()  # 类实例化
    w, _ = wordcloud.get_wordcloud(comment)  # 生成词云图,并获取标题的前两个词

    w.to_file('D://Data/WordCloud/comment/{0}.png'.format(number))
    print('comment {0} wordcloud has been saved'.format(number))

    return 'a'


app.run(host='0.0.0.0')  # 对外开放接口
