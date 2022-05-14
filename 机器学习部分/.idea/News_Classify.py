import pickle
import pandas as pd
import re
import jieba
import numpy as np


class Classify:

    def classify(self, title):
        news_test = pd.DataFrame(title, columns=['title'], index=['0'])

        # 正则表达式剔除标点 开始-------------------------------------------------------------------------------------------
        re_obj = re.compile(r"['~`!#$%^&*()_+-=|\';:/.,?><~·！@#￥%……&*（）——+-=“：’；、。，？》《{}'：【】《》‘’“” \s\t]+")

        def clear(text):
            return re_obj.sub("", text)

        news_test["title"] = news_test["title"].apply(clear)

        # 正则表达式剔除标点 结束-------------------------------------------------------------------------------------------

        # jieba分词 开始-------------------------------------------------------------------------------------------------
        def cut_word(text):  # 分词，使用jieba的lcut方法分割词，生成一个列表，
            # cut()  生成一个生成器， 不占用空间或者说占用很少的空间，使用list()可以转换成列表
            return jieba.lcut(text)

        news_test["title"] = news_test["title"].apply(cut_word)

        # jieba分词 结束-------------------------------------------------------------------------------------------------

        # 剔除停止词 开始-------------------------------------------------------------------------------------------------
        def get_stopword():  # 删除停用词，就是在文中大量出现，对分类无用的词 降低存储和减少计算时间
            with open('stop_words.txt', encoding='utf-8') as f:
                stop_words = f.read()
            return stop_words

        def remove_stopword(words):
            return [word for word in words if word not in stopword]

        stopword = get_stopword()
        news_test["title"] = news_test["title"].apply(remove_stopword)

        # 剔除停止词 结束-------------------------------------------------------------------------------------------------

        # 将标题转换为字符串形式
        def join(text_list):
            return " ".join(text_list)

        news_test["title"] = news_test["title"].apply(join)
        X_test = news_test["title"]

        # 对X进行文本向量化处理（TF-IDF）
        # TFIDF的主要思想是：如果某个词或短语在一篇文章中出现的频率TF高，并且在其他文章中很少出现，则认为此词或者短语具有很好的类别区分能力，适合用来分类。
        # 通过提取已经训练并保存好的模型来加快运行速度
        with open('./vec.pkl', 'rb') as f:
            vec = pickle.load(f)
        x_test_vec = vec.transform(X_test)

        # 将数据转换为float32的形式
        x_test_vec = x_test_vec.astype(np.float32)

        # 通过selector选择出训练夏鸥过更好的数据
        # 这里模型是通过直接读取的方式
        with open('./selector.pkl', 'rb') as f:
            selector = pickle.load(f)
        x_test_vec = selector.transform(x_test_vec)

        # 模型的读取
        with open('./clf_Logistic.pkl', 'rb') as f:
            clf_logistic = pickle.load(f)

        return clf_logistic.predict(x_test_vec)

