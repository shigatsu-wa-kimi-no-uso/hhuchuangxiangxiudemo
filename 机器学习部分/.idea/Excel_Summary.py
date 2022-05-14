import pandas as pd
import xlrd

data = pd.read_excel('./news_train_data/竞赛数据集(文化).xls', converters={'id': str})



def Summary(f):
    summary = ''
    f = f + '。'
    for i in range(len(f)):
        if list(f)[i] == '。' or list(f)[i] == '？' or list(f)[i] == '！':
            cut = i

            # if cut > 20:
            for k in range(cut + 1):
                summary = summary + list(f)[k]
            return summary
            # if cut <= 20:
            #     for k in range(len(f)):
            #         if list(f)[cut + 1 + k] == '。' or list(f)[cut + 1 + k] == '？' or list(f)[cut + 1 + k] == '！':
            #             cut2 = cut + 1 + k
            #             for j in range(cut2 + 1):
            #                 summary = summary + list(f)[j]
            #             return [summary]
            #         else:
            #             return [f]
        else:
            pass
    else:
        return f


data['content'] = data['content'].apply(Summary)
# print(data['content'])
writer = pd.ExcelWriter('./news_train_data/竞赛数据集(文化).xls', mode='a', engine='openpyxl')
wb = writer.book
wb.remove(wb['Sheet2'])
data.to_excel(writer, sheet_name='Sheet1')
writer.save()
