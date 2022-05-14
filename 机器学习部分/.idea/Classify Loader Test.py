import pickle
import pandas as pd
import re
import jieba
import numpy as np
import matplotlib.pyplot as plt

plt.rcParams['font.sans-serif'] = 'SimHei' # matplotlib文本设置为黑体


news_test = pd.read_excel("./news_train_data/test_data_3.xls", converters={'id': str})
re_obj = re.compile(r"['~`!#$%^&*()_+-=|\';:/.,?><~·！@#￥%……&*（）——+-=“：’；、。，？》《{}'：【】《》‘’“” \s\t]+")


def clear(text):
    return re_obj.sub("", text)


news_test["title"] = news_test["title"].apply(clear)


def cut_word(text):  # 分词，使用jieba的lcut方法分割词，生成一个列表，
    # cut()  生成一个生成器， 不占用空间或者说占用很少的空间，使用list()可以转换成列表
    return jieba.lcut(text)


news_test["title"] = news_test["title"].apply(cut_word)


def get_stopword():  # 删除停用词，就是在文中大量出现，对分类无用的词 降低存储和减少计算时间
    with open('stop_words.txt', encoding='utf-8') as f:
        stop_words = f.read()
    return stop_words


def remove_stopword(words):
    return [word for word in words if word not in stopword]


stopword = get_stopword()

news_test["title"] = news_test["title"].apply(remove_stopword)


# 文本向量化需要传递空格分开的字符串数组类型

def join(text_list):
    return " ".join(text_list)


news_test["title"] = news_test["title"].apply(join)

news_test["tag"] = news_test["tag"].map({"news_culture": 5,
                                         'news_tech': 4,
                                         'news_military': 1,
                                         'news_world': 3,
                                         'news_finance': 2})


X_test = news_test["title"]
y_test = news_test["tag"]


with open('./vec.pkl', 'rb') as f:
    vec = pickle.load(f)
X_test_vec = vec.transform(X_test)

with open('./X_train_vec.txt', 'rb') as f:
    X_train_vec = pickle.load(f)

with open('./selector.pkl', 'rb') as f:
    selector = pickle.load(f)

X_test_vec = X_test_vec.astype(np.float32)
X_test_vec = selector.transform(X_test_vec)


with open('./clf_Logistic.pkl', 'rb') as f:
    clf2 = pickle.load(f)

from sklearn.metrics import classification_report
y_hat = clf2.best_estimator_.predict(X_test_vec)
print(classification_report(y_test, y_hat))


