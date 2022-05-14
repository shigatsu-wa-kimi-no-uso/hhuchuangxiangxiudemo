import jieba
import wordcloud
from wordcloud import STOPWORDS
f = open('新时代中国特色社会主义.txt', 'r', encoding='utf-8')
t = f.read()
f.close()
ls = jieba.lcut(t)
print(ls)
txt = ' '.join(ls)
w = wordcloud.WordCloud(width=1000, height=700,
                        font_path='msyh.ttc',
                        background_color='white', max_words=100,
                        font_step=2,
                        )
stopwords = STOPWORDS
stopwords.add('的')
stopwords.add('以')
stopwords.add('和')
stopwords.add('在')
stopwords.add('了')
stopwords.add('为')
stopwords.add('这个')
stopwords.add('个')
stopwords.add('一个')
stopwords.add('从')
stopwords.add('中')
stopwords.add('是')
stopwords.add('与')

w.generate(txt)
w.to_file('gwcloud.png')
