import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import jieba
import pickle
from sklearn.feature_extraction.text import TfidfVectorizer  # 文本向量化
from sklearn.feature_selection import f_classif
from itertools import chain
from collections import Counter
import sklearn.preprocessing as pre_processing
import re  # 文本的处理 sub调用编译后的正则对象对文本进行处理

plt.rcParams['font.sans-serif'] = 'SimHei' # matplotlib文本设置为黑体

# 获取训练集和测试集
news_train = pd.read_excel("./news_train_data/train_data_3.xls", converters={'id': str})
news_test = pd.read_excel("./news_train_data/test_data_3.xls", converters={'id': str})

# 通过正则表达式剔除标点符号 开始--------------------------------------------------------------------------------------------
re_obj = re.compile(r"['~`!#$%^&*()_+-=|\';:/.,?><~·！@#￥%……&*（）——+-=“：’；、。，？》《{}'：【】《》‘’“” \s\t]+")


def clear(text):
    return re_obj.sub("", text)


news_train["title"] = news_train["title"].apply(clear)
news_test["title"] = news_test["title"].apply(clear)
# 通过正则表达式剔除标点符号 结束--------------------------------------------------------------------------------------------

# jieba分词 开始---------------------------------------------------------------------------------------------------------


def cut_word(text):  # 分词，使用jieba的lcut方法分割词，生成一个列表，
    # cut()  生成一个生成器， 不占用空间或者说占用很少的空间，使用list()可以转换成列表
    return jieba.lcut(text)


news_train["title"] = news_train["title"].apply(cut_word)
news_test["title"] = news_test["title"].apply(cut_word)
# jieba分词 结束--------------------------------------------------------------------------------------------------------

# 停止词剔除 开始---------------------------------------------------------------------------------------------------------


def get_stopword():  # 删除停用词，就是在文中大量出现，对分类无用的词 降低存储和减少计算时间
    with open('stop_words.txt', encoding='utf-8') as f:
        stop_words = f.read()
    return stop_words


def remove_stopword(words):
    return [word for word in words if word not in stopword]


stopword = get_stopword()
news_train["title"] = news_train["title"].apply(remove_stopword)
news_test["title"] = news_test["title"].apply(remove_stopword)
# 停止词剔除 结束---------------------------------------------------------------------------------------------------------

# # -------------------------------------------下面是内容展示
# print(news['title'])
# # 数据探索  描述性分析
# # tag统计
# t = news["tag"].value_counts()
# print(t)
# t.plot(kind="bar")
# plt.show()
#
#  # -----------------------------------------下面是词频展示
# li_2d = news["title"].tolist()  # 转二维数组
#
# # 二维数组转一维数组
# li_1d = list(chain.from_iterable(li_2d))
# print(f"词汇总量：{len(li_1d)}")
# c = Counter(li_1d)
# print(f"不重复词汇数量：{len(c)}")
# print(c.most_common(15))
# common = c.most_common(15)
# d = dict(common)
# plt.figure(figsize=(15, 5))
# plt.bar(d.keys(), d.values())
# plt.show()
# --------------------------------------------------------------


# 文本向量化需要传递空格分开的字符串数组类型
# 将数据转换为字符串类型
def join(text_list):
    return " ".join(text_list)

# 将标题转换为字符串形式，标签通过类似于one-hot编码进行处理
news_train["title"] = news_train["title"].apply(join)

news_train["tag"] = news_train["tag"].map({"news_culture": 5,
                                           'news_tech': 4,
                                           'news_military': 1,
                                           'news_world': 3,
                                           'news_finance': 2})
news_test["title"] = news_test["title"].apply(join)

news_test["tag"] = news_test["tag"].map({"news_culture": 5,
                                         'news_tech': 4,
                                         'news_military': 1,
                                         'news_world': 3,
                                         'news_finance': 2})
# 定义训练集和测试集的x和y
X_train= news_train["title"]
y_train = news_train["tag"]
X_test= news_test["title"]
y_test = news_test["tag"]

# 对X进行文本向量化处理（TF-IDF）
# TF-IDF的主要思想是：如果某个词或短语在一篇文章中出现的频率TF高，并且在其他文章中很少出现，则认为此词或者短语具有很好的类别区分能力，适合用来分类。
vec = TfidfVectorizer(ngram_range=(1, 2))
X_train_vec = vec.fit_transform(X_train)
X_test_vec = vec.transform(X_test)
# 保存模型
with open('./vec.pkl', 'wb') as f:
    pickle.dump(vec, f)

# 通过f_classif获取到对分类更有效的数据
f_classif(X_train_vec, y_train)
from sklearn.feature_selection import SelectKBest

# 转换为float32形式
X_train_vec = X_train_vec.astype(np.float32)
X_test_vec = X_test_vec.astype(np.float32)

# 数据选取及转换
selector = SelectKBest(f_classif, k=1200)  # 维度为1200
selector.fit(X_train_vec, y_train)
X_train_vec = selector.transform(X_train_vec)
X_test_vec = selector.transform(X_test_vec)

# 保存处理好的X_train_vec和selector模型
# （通过保存和提取的方式可以比再次对数据进行上述处理会提高很多的效率）
with open('./X_train_vec.txt', 'wb') as f:
    pickle.dump(X_train_vec, f)
with open('./selector.pkl', 'wb') as f:
    pickle.dump(selector, f)

# 对数据进行训练
# 这里总共采取了3种训练方法，并选取除了训练效果最好的一种
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import GridSearchCV
from sklearn.metrics import classification_report, f1_score

# LogisticRegression Params --------------------------------------------
param = [{'penalty': ['l1', 'l2'], 'C': [0.1, 1, 10],
         'solver': ['liblinear']},
         {'penalty': ['elasticnet'], 'C': [0.1, 1, 10],
          'solver': ['saga'], 'l1_ratio': [0.5], 'max_iter': [200]}]

# 以下为LogisticRegression的部分参数解释
# peanlty----正则化参数，对应着三种正则化的方式
# l1对应着最后加上入*|theta|求和， l2对应着最后加上入*(theta)**2
# elasticnet为l1和l2的组合， 即二者同时存在，可以通过l1_ratio调整二者前面的系数, a[入*|theta|求和]+a[入*(theta)**2]
# C-------正则强度的倒数，较小的值指定更强的正则化
# solver-------优化算法选择参数：liblinear：坐标轴下降法来迭代优化损失函数；saga：线性收敛的随机优化算法的的变重。
# 优化算法选择参数，五种取值：newton-cg,lbfgs,liblinear,sag,saga。default = liblinear。
# liblinear适用于小数据集，而sag和saga适用于大数据集因为速度更快。
# 如果是L2正则化，那么4种可选的算法{‘newton-cg’, ‘lbfgs’, ‘liblinear’, ‘sag’}都可以选择。但是如果penalty是L1正则化的话，就只能选择‘liblinear’了。
# l1_ratio-------可以调整l1和l2的凸组合（一类特殊的线性组合），该参数用于elaticnet
# max_iter------最大迭代次数

# ---------------------------------------------------------------------
# KNN算法
# from sklearn.neighbors import KNeighborsClassifier
# param = {'n_neighbors': [5, 7],
#           'weights': ['uniform', 'distance'],
#           'p': [2]}
# --------------------------------------------------------------------
# 朴素贝叶斯 Params
# from sklearn.naive_bayes import GaussianNB, BernoulliNB, MultinomialNB, ComplementNB
# from sklearn.pipeline import Pipeline
# from sklearn.preprocessing import FunctionTransformer
#
# steps = [('dense', FunctionTransformer(func=lambda X: X.toarray(), accept_sparse=True)),
#         ('model', None)
#         ]
# pipe = Pipeline(steps=steps)
# param = {'model': [GaussianNB(), BernoulliNB(), MultinomialNB(), ComplementNB()]}

# --------------------------------------------------------------------
gs = GridSearchCV(estimator=LogisticRegression(),
                  param_grid=param,  # 正则化参数
                  cv=2,  # crossvalidation 交叉验证
                  scoring='f1_macro',  # 计算得分的方法
                  n_jobs=-1,  # 最大并行数和核CPU一致
                  verbose=10  # 日志冗长度：对每个子模型都输出
                  )
# --------------------------------------------------------------------
# gs = GridSearchCV(estimator=KNeighborsClassifier(), param_grid=param, cv=2,
#                   scoring='f1', n_jobs=-1, verbose=10)
# --------------------------------------------------------------------
# gs = GridSearchCV(estimator=pipe, param_grid=param,
#                   cv=2, scoring='f1', n_jobs=2, verbose=10)
# --------------------------------------------------------------------

gs.fit(X_train_vec, y_train)
print('模型训练结束')
# y_hat为预测值， y_test为标签值
y_hat = gs.best_estimator_.predict(X_test_vec)
print(f1_score(y_test, y_hat, average='micro'))
print(classification_report(y_test, y_hat))



# with open('./clf_KNC.pkl', 'wb') as f:
#     pickle.dump(gs, f)

with open('./clf_Logistic.pkl', 'wb') as f:
    pickle.dump(gs, f)

# with open('./clf_bayes.pkl', 'wb') as f:
#     pickle.dump(gs, f)

print('模型保存成功')

