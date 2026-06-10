# AI Chat 功能配置

## 一、配置 API Key

### 方式一：环境变量（推荐）

在 Linux 服务器上设置环境变量：

```bash
# 临时生效
export AI_API_KEY=your-api-key-here
export AI_BASE_URL=https://api.openai.com  # 或其他兼容 API 地址

# 永久生效（添加到 ~/.bashrc）
echo 'export AI_API_KEY=your-api-key-here' >> ~/.bashrc
source ~/.bashrc
```

### 方式二：application-private.yml

在 `/opt/bms/application-private.yml` 中添加：

```yaml
ai:
  api-key: your-api-key-here
  base-url: https://api.openai.com
  model: gpt-3.5-turbo
  daily-limit: 10
```

---

## 二、支持国内 AI 模型

如果使用国内模型，需要修改 base-url：

| 模型 | base-url 示例 |
|------|--------------|
| OpenAI 官方 | https://api.openai.com |
| 阿里通义 | https://dashscope.aliyuncs.com |
| 百度文心 | https://aip.baidubce.com |
| 智谱 ChatGLM | https://open.bigmodel.cn |
| 中转 API | 你的中转地址 |

---

## 三、支持的模型

| 模型 | 说明 |
|------|------|
| gpt-3.5-turbo | OpenAI 官方，便宜快速 |
| gpt-4 | OpenAI 官方，更智能但贵 |
| qwen-turbo | 阿里通义，便宜快速 |
| chatglm-4 | 智谱，更智能 |

---

## 四、调试

启动后端后，可以测试接口：

```bash
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{"message":"推荐几本编程书"}'
```
