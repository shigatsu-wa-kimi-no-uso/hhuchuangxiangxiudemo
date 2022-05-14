class Get_summary:

    def get_summary(self, text):
        f = text
        f = f.replace('\n', '')  # 删掉换行符
        f = f.replace('，', '')  # 删掉逗号
        f = f.replace(';', '')  # 删掉分号
        f = f.replace('：', '')  # 删掉冒号
        f = f.replace('#', '')  # 删掉#
        f = f.replace('.', '')  # 删掉.
        f = f.replace(':', '')  # 删掉-
        f = f.replace('(', '')  # 删掉-
        f = f.replace(')', '')  # 删掉-
        f = f.replace('!', '')  # 删掉-
        f = f.replace('/', '')  # 删掉-
        f = f.replace('\\', '')  # 删掉-
        f = f.replace('[', '')  # 删掉-
        f = f.replace('-', '')  # 删掉-
        f = f.replace(']', '')  # 删掉-

        for i in range(26):  # 删掉大小写的英文字母
            f = f.replace(chr(97 + i), '')
            f = f.replace(chr(65 + i), '')
        for i in range(10):  # 删掉数字
            f = f.replace('{0}'.format(i), '')

        # 下面是获取摘要
        # 主要思路
        # 先获取到第一个句号的内容，如果大于65个字，则将其作为summary
        # 若小于65个字，则再获取到第二个句号的内容，将这两句一起作为summary
        summary = ''
        for i in range(len(f)):
            if list(f)[i] == '。' or list(f)[i] == '？' or list(f)[i] == '！':
                cut = i
                if cut > 65:
                    for k in range(cut+1):
                        summary = summary + list(f)[k]
                    return summary
                if cut <= 65:
                    for k in range(len(f)):
                        if list(f)[cut+1+k] == '。' or list(f)[cut+1+k] == '？' or list(f)[cut+1+k] == '！':
                            cut2 = cut+1+k
                            for j in range(cut2+1):
                                summary = summary + list(f)[j]
                            return summary




