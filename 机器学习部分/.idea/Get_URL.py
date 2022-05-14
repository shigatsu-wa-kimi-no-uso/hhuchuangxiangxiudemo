class Get_url():

    def get_url(self,text):
        f = list(text)
        for i in range(len(f)):
            if f[i:i+5] == ['h', 't', 't', 'p', ':']:
                head = i
                url = ''
                for j in range(100):
                    if f[head+j:head+j+4] == ['.', 'p', 'n', 'g'] or f[head+j:head+j+4] == ['.', 'j', 'p', 'g'] or f[head+j:head+j+4] == ['.', 'g', 'i', 'f'] or f[head+j:head+j+4] == ['.', 't', 'i', 'f'] or f[head+j:head+j+4] == ['.', 'b', 'm', 'p'] or f[head+j:head+j+4] == ['.', 'w', 'e', 'b', 'p']:
                        for k in f[head:head+j+4]:
                            url = url + k
                        return url
            if i == len(f)-1:
                return 'NULL'

# 模块单独调用测试
# url_getter = Get_url()
# url_ = url_getter.get_url('acbabcabcabcbahttp://10.2.66.10/src/img/123456.pngabcbabcabcb')
# print(url_)
