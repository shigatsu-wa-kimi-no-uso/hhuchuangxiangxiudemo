import requests

if __name__ =='__main__':
    url = 'https://news.cctv.com/mobile/'
    headers = {
        'User-Agent': 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Mobile Safari/537.36 Edg/100.0.1185.36'
    }
    response = requests.get(url, headers=headers)
    news_data = response.text
    print(news_data)