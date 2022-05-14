from xlwt import Workbook
import os

book = Workbook(encoding='utf-8')
sheet = book.add_sheet('1')

sheet.write(0, 1, 'tag')
sheet.write(0, 2, 'title')
sheet.write(0, 3, 'content')
sheet.write(0, 0, 'number')

path = './/newstitle'
count = len(os.listdir(path))

for i in range(count):
    title_path = './/newstitle//newstitle{A}.txt'.format(A=i+1)
    content_path = './/newstxt//newstxt{B}.txt'.format(B=i+1)
    tag_path = './/newstag//newstag{A}.txt'.format(A=i+1)

    f = open(title_path, 'r', encoding='utf-8')
    text1 = f.read()
    sheet.write(1+i, 2, text1)  # 行，列，属性值 (1,1)为B2元素，从0开始计数
    f.close()

    f = open(content_path, 'r', encoding='utf-8')
    text2 = f.read()
    if text2 == '':
        text2 = text1
    text2 = text2.replace('\n', '')  # 删掉换行符
    text2 = text2.replace('，', '')  # 删掉逗号
    text2 = text2.replace('。', '')  # 删掉句号
    text2 = text2.replace('？', '')  # 删掉问号
    text2 = text2.replace(';', '')  # 删掉分号
    text2 = text2.replace('：', '')  # 删掉冒号
    sheet.write(1 + i, 3, text2)  
    f.close()

    f = open(tag_path, 'r', encoding='utf-8')
    text3 = f.read()
    sheet.write(1 + i, 1, text3)
    f.close()

    sheet.write(1 + i, 0, i)

book.save('news.xls') # 一定要保存

