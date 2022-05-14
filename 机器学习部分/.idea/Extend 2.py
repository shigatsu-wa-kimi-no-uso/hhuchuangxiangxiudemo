import enum
from typing import Text
import torch
from torchtext.datasets import AG_NEWS

#######################################################################
# Step1 加载数据集
#######################################################################

# 【数据集介绍】AG_NEWS, 新闻语料库，仅仅使用了标题和描述字段，
#             包含4个大类新闻:World、Sports、Business、Sci/Tec。
# 【样本数据】 120000条训练样本集（train.csv)， 7600测试样本数据集(test.csv);
#            每个类别分别拥有 30,000 个训练样本及 1900 个测试样本。
#

train_iter = AG_NEWS(split='train')

print(next(train_iter))
print(next(train_iter))
print(next(train_iter))

#######################################################################
# Step2 分词和构建词汇表
#######################################################################
from torchtext.data.utils import get_tokenizer
from torchtext.vocab import build_vocab_from_iterator

tokenizer = get_tokenizer('basic_english')  # 基本的英文分词器
train_iter = AG_NEWS(split="train")  # 训练数据加载器


# 分词生成器
def yield_tokens(data_iter):
    for _, text in data_iter:
        yield tokenizer(text)


# 根据训练数据构建词汇表
vocab = build_vocab_from_iterator(yield_tokens(train_iter), specials=["<unk>"])
vocab.set_default_index(vocab["<unk>"])  # 设置默认索引，当某个单词不在词汇表vocab时（OOV)，返回该单词索引

# 词汇表会将token映射到词汇表中的索引上
print(vocab(["here", "is", "an", "example"]))

##########################################################################
# Step3 构建数据加载器 dataloader
##########################################################################
# text_pipeline将一个文本字符串转换为整数List, List中每项对应词汇表voca中的单词的索引号
text_pipeline = lambda x: vocab(tokenizer(x))

# label_pipeline将label转换为整数
label_pipeline = lambda x: int(x) - 1

# pipeline example
print(text_pipeline("hello world! I'am happy"))
print(label_pipeline("10"))

# 加载数据集合，转换为张量
from torch.utils.data import DataLoader

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


def collate_batch(batch):
    label_list, text_list, offsets = [], [], [0]
    for (_label, _text) in batch:
        label_list.append(label_pipeline(_label))
        processed_text = torch.tensor(text_pipeline(_text), dtype=torch.int64)
        text_list.append(processed_text)
        offsets.append(processed_text.size(0))
    label_list = torch.tensor(label_list, dtype=torch.int64)
    offsets = torch.tensor(offsets[:-1]).cumsum(dim=0)
    text_list = torch.cat(text_list)
    return label_list.to(device), text_list.to(device), offsets.to(device)


train_iter = AG_NEWS(split='train')
dataloader = DataLoader(train_iter, batch_size=8, shuffle=False, collate_fn=collate_batch)

###########################################################################
# Step4 定义神经网络模型：由一个EmbeddingBag 隐藏层和一个线性全连接层组成
###########################################################################
from torch import nn


class TextClassificationModel(nn.Module):
    def __init__(self, vocab_size, embed_dim, num_class):
        super(TextClassificationModel, self).__init__()
        self.embedding = nn.EmbeddingBag(vocab_size, embed_dim, sparse=True)
        self.fc = nn.Linear(embed_dim, num_class)
        self.init_weight()

    def init_weight(self):
        """
        初始化参数权重
        """
        init_range = 0.5
        self.embedding.weight.data.uniform_(-init_range, init_range)  # 隐藏层权重参数初始化[-0.5, 0.5]
        self.fc.weight.data.uniform_(-init_range, init_range)  # 全连接层权重参数初始化[0.5, 0.5]
        self.fc.bias.data.zero_()  # 全连接层偏置权重b置为0

    def forward(self, text, offsets):
        """
        前向传播函数
        """
        embedded = self.embedding(text, offsets)
        return self.fc(embedded)


###########################################################################
# Step5 定义模型训练和评估函数
###########################################################################
train_iter = AG_NEWS(split="train")
num_class = len(set([label for (label, text) in train_iter]))
print("num_class:", num_class)

vocab_size = len(vocab)
emsize = 64

# 创建隐藏层为64的TextClassificationModel
model = TextClassificationModel(vocab_size, emsize, num_class)

# 对模型进行训练
import time


def train(dataloader):
    model.train()
    total_acc, total_count = 0, 0
    log_interval = 500
    start_time = time.time()

    for idx, (label, text, offsets) in enumerate(dataloader):
        optimizer.zero_grad()  # 参数优化器梯度置0

        predited_label = model(text, offsets)

        # 计算梯度损失
        loss = criterion(predited_label, label)

        # 梯度反向传播
        loss.backward()

        # 参数更新
        torch.nn.utils.clip_grad_norm_(model.parameters(), 0.1)
        optimizer.step()

        # 计算精度
        total_acc += (predited_label.argmax(1) == label).sum().item()
        total_count += label.size(0)

        if idx % log_interval == 0 and idx > 0:
            elapsed = time.time() - start_time
            print('| epoch {:3d} | {:5d}/{:5d} batches '
                  '| accuracy {:8.3f}'.format(epoch, idx, len(dataloader),
                                              total_acc / total_count))
            total_acc, total_count = 0, 0
            start_time = time.time()


# 评估模型精度
def evaluate(dataloader):
    model.eval()
    total_acc, total_count = 0, 0

    with torch.no_grad():
        for idx, (label, text, offsets) in enumerate(dataloader):
            predited_label = model(text, offsets)
            loss = criterion(predited_label, label)
            total_acc += (predited_label.argmax(1) == label).sum().item()
            total_count += label.size(0)
    return total_acc / total_count


############################################################################
# Step6 训练模型
###########################################################################
from torch.utils.data.dataset import random_split
from torchtext.data.functional import to_map_style_dataset

# 定义超参数
EPOCHS = 10  # 训练次数
LR = 5  # 学习率
BATCH_SIZE = 64  # 训练批量数

# 定义损失函数： 交叉熵损失函数
criterion = torch.nn.CrossEntropyLoss()

# 定义优化器： 随机梯度下降SGD
optimizer = torch.optim.SGD(model.parameters(), lr=LR)

# 定义学习策略：step_size=1，gamma=0.1, 即每个step_size之后，Epoch的学习率衰减0.1，
# lr = 5     if epoch =1
# lr = 0.5    if epoch = 2
# lr = 0.05   if epoch = 3
scheduler = torch.optim.lr_scheduler.StepLR(optimizer, 1.0, gamma=0.1)

total_accu = None  # 精度
train_iter, test_iter = AG_NEWS()

train_dataset = to_map_style_dataset(train_iter)  # 训练样本集
test_dataset = to_map_style_dataset(test_iter)  # 测试样本集

num_train = int(len(train_dataset) * 0.95)
split_train, split_valid = random_split(train_dataset, [num_train, len(train_dataset) - num_train])

train_dataloader = DataLoader(split_train, batch_size=BATCH_SIZE, shuffle=True, collate_fn=collate_batch)
valid_dataloader = DataLoader(split_valid, batch_size=BATCH_SIZE, shuffle=True, collate_fn=collate_batch)
test_dataloader = DataLoader(test_dataset, batch_size=BATCH_SIZE, shuffle=True, collate_fn=collate_batch)

# 迭代训练模型
for epoch in range(1, EPOCHS + 1):
    epoch_start_time = time.time()
    train(train_dataloader)
    accu_val = evaluate(valid_dataloader)

    if total_accu is not None and total_accu > accu_val:
        scheduler.step()
    else:
        total_accu = accu_val

    print("-" * 59)
    print('| end of epoch {:3d} | time: {:5.2f}s | '
          'valid accuracy {:8.3f} '.format(epoch,
                                           time.time() - epoch_start_time,
                                           accu_val))
    print("-" * 59)

###########################################################################
# Step7 模型评估
###########################################################################
print('Checking the results of test dataset.')
accu_test = evaluate(test_dataloader)
print('test accuracy {:8.3f}'.format(accu_test))

###########################################################################
# Step8 预测推理
###########################################################################
ag_news_label = {
    1: "World",
    2: "Sports",
    3: "Business",
    4: "Sci/Tec"
}


def predict(text, text_pipeline):
    with torch.no_grad():
        text = torch.tensor(text_pipeline(text))
        output = model(text, torch.tensor([0]))
        return output.argmax(1).item() + 1


ex_text_str = "MEMPHIS, Tenn. – Four days ago, Jon Rahm was \
    enduring the season’s worst weather conditions on Sunday at The \
    Open on his way to a closing 75 at Royal Portrush, which \
    considering the wind and the rain was a respectable showing. \
    Thursday’s first round at the WGC-FedEx St. Jude Invitational \
    was another story. With temperatures in the mid-80s and hardly any \
    wind, the Spaniard was 13 strokes better in a flawless round. \
    Thanks to his best putting performance on the PGA Tour, Rahm \
    finished with an 8-under 62 for a three-stroke lead, which \
    was even more impressive considering he’d never played the \
    front nine at TPC Southwind."

model = model.to("cpu")
print("This is a %s news" % ag_news_label[predict(ex_text_str, text_pipeline)])