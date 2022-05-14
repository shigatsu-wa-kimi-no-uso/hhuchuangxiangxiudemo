import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


size = 18
fig_length = 15
fig_width = 5

plt.rcParams['font.sans-serif'] = 'SimHei'  # matplotlib文本设置为黑体
data = pd.read_excel("./global epidemic.xls", converters={'id': str})

data_area = np.array(data['area'])
data_addition = data['addition']
data_total = data['total']
data_heal = data['heal']
data_death = data['death']

# 国际疫情状况
fig1 = plt.figure('国际累计人数', figsize=(fig_length, fig_width))
x = np.arange(size)
y_total = data_total[:size]
new_ticks = data_area
plt.xticks([i for i in range(size)], new_ticks[:size])
plt.bar(x, y_total, facecolor='#9999ff', edgecolor='white', label='国际累计人数')
for x, y in zip(x, y_total):  # zip起来之后可以使之后每次都输出x和y两个值
    plt.text(x, +y, '%d' % y, ha='center', va='bottom')

fig2 = plt.figure('国际死亡人数', figsize=(fig_length, fig_width))
x = np.arange(size)
y_death = data_death[:size]
new_ticks = data_area
plt.xticks([i for i in range(size)], new_ticks[:size])
plt.bar(x, y_death, facecolor='#9999ff', edgecolor='white', label='国际死亡人数')
for x, y in zip(x, y_death):  # zip起来之后可以使之后每次都输出x和y两个值
    plt.text(x, +y, '%d' % y, ha='center', va='bottom')

fig3 = plt.figure('国际治愈人数', figsize=(fig_length, fig_width))
x = np.arange(size)
y_heal = data_heal[:size]
new_ticks = data_area
plt.xticks([i for i in range(size)], new_ticks[:size])
plt.bar(x, y_heal, facecolor='#9999ff', edgecolor='white', label='国际治愈人数')
for x, y in zip(x, y_heal):  # zip起来之后可以使之后每次都输出x和y两个值
    plt.text(x, +y, '%d' % y, ha='center', va='bottom')

fig4 = plt.figure('国际新增人数', figsize=(fig_length, fig_width))
x = np.arange(size)
y_addition = data_addition[:size]
new_ticks = data_area
plt.xticks([i for i in range(size)], new_ticks[:size])
plt.bar(x, y_addition, facecolor='#9999ff', edgecolor='white', label='国际新增人数')
for x, y in zip(x, y_addition):  # zip起来之后可以使之后每次都输出x和y两个值
    plt.text(x, +y, '%d' % y, ha='center', va='bottom')

# 国内疫情状况
data = pd.read_excel("./domestic epidemic.xls", converters={'id': str})

data_area = np.array(data['area'])
data_addition = data['addition']
data_total = data['total']
data_heal = data['heal']
data_death = data['death']

fig5 = plt.figure('国内累计人数', figsize=(fig_length, fig_width))
x = np.arange(size)
y_total = data_total[:size]
new_ticks = data_area
plt.xticks([i for i in range(size)], new_ticks[:size])
plt.bar(x, y_total, facecolor='#9999ff', edgecolor='white', label='国内累计人数')
for x, y in zip(x, y_total):  # zip起来之后可以使之后每次都输出x和y两个值
    plt.text(x, +y, '%d' % y, ha='center', va='bottom')


fig6 = plt.figure('国内死亡人数', figsize=(fig_length, fig_width))
x = np.arange(size)
y_death = data_death[:size]
new_ticks = data_area
plt.xticks([i for i in range(size)], new_ticks[:size])
plt.bar(x, y_death, facecolor='#9999ff', edgecolor='white', label='国内死亡人数')
for x, y in zip(x, y_death):  # zip起来之后可以使之后每次都输出x和y两个值
    plt.text(x, +y, '%d' % y, ha='center', va='bottom')

fig7 = plt.figure('国内治愈人数', figsize=(fig_length, fig_width))
x = np.arange(size)
y_heal = data_heal[:size]
new_ticks = data_area
plt.xticks([i for i in range(size)], new_ticks[:size])
plt.bar(x, y_heal, facecolor='#9999ff', edgecolor='white', label='国内治愈人数')
for x, y in zip(x, y_heal):  # zip起来之后可以使之后每次都输出x和y两个值
    plt.text(x, +y, '%d' % y, ha='center', va='bottom')

fig8 = plt.figure('国内新增人数', figsize=(fig_length, fig_width))
x = np.arange(size)
y_addition = data_addition[:size]
new_ticks = data_area
plt.xticks([i for i in range(size)], new_ticks[:size])
plt.bar(x, y_addition, facecolor='#9999ff', edgecolor='white', label='国内新增人数')
for x, y in zip(x, y_addition):  # zip起来之后可以使之后每次都输出x和y两个值
    plt.text(x, +y, '%d' % y, ha='center', va='bottom')

plt.show()
# fig1.savefig('./epidemic photos/fig1.jpg')
# fig2.savefig('./epidemic photos/fig2.jpg')
# fig3.savefig('./epidemic photos/fig3.jpg')
# fig4.savefig('./epidemic photos/fig4.jpg')
# fig5.savefig('./epidemic photos/fig5.jpg')
# fig6.savefig('./epidemic photos/fig6.jpg')
# fig7.savefig('./epidemic photos/fig7.jpg')
# fig8.savefig('./epidemic photos/fig8.jpg')

print('over')
