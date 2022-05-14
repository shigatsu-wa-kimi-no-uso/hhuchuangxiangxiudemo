import jieba
import wordcloud
import imageio
from collections import Counter
import matplotlib.pyplot as plt

# 以下为封装好的生成词云图代码

with open('./新时代中国特色社会主义.txt', 'r', encoding='utf-8') as f:
    f = f.read()
f = f.replace('\n', '')  # 删掉换行符
f = f.replace('，', '')  # 删掉逗号
f = f.replace('。', '')  # 删掉句号
f = f.replace('？', '')  # 删掉问号
f = f.replace(';', '')  # 删掉分号
f = f.replace('：', '')  # 删掉冒号

# 定义停止词
stop_words = open('stop_words.txt', encoding='utf-8').read().split()  # split按照空格，逗号，分号等进行split

# 图片读取
image_mask = imageio.imread('China.jpg')
# 剔除停止词和单个的字，并生成一个列表
word_list = [
        w for w in jieba.cut(f)
        if w not in set(stop_words) and len(w) > 1
        ]
# 根据生成的列表统计词频
freq = dict(Counter(word_list))

w = wordcloud.WordCloud(width=4000, height=2800,  # 设置词云图长宽
                        font_path='msyh.ttc',  # 设置字体样式
                        background_color='white',  # 设置背景颜色
                        max_words=30,  # 设置最大词容量
                        mode="RGB",  # 设置图片样式
                        mask=image_mask,  # 设置图片的形状
                        max_font_size=50  # 设置字体的最大尺寸
                        )
# 根据词频生成词云图
w.generate_from_frequencies(freq)
# 调整清晰度
plt.figure(dpi=1000)

# 保存图片
plt.show()

for key, value in freq.items():
    if value == max(freq.values()):
        freq_max = key
print(freq)
print(freq_max)
print(word_list[0:2])
