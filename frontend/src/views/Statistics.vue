<template>
  <div class="statistics-container">
    <div class="page-header">
      <h2 class="page-title">数据统计</h2>
      <el-button type="primary" @click="exportReport" class="export-btn">
        <el-icon><Download /></el-icon>
        导出运营报表
      </el-button>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon books-icon">
            <el-icon :size="40"><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalBooks || 0 }}</div>
            <div class="stat-label">图书总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon borrowed-icon">
            <el-icon :size="40"><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalBorrowed || 0 }}</div>
            <div class="stat-label">已借出</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon available-icon">
            <el-icon :size="40"><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalAvailable || 0 }}</div>
            <div class="stat-label">在馆可借</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon records-icon">
            <el-icon :size="40"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalBorrowRecords || 0 }}</div>
            <div class="stat-label">借阅记录</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>图书分类分布</span>
            </div>
          </template>
          <div ref="categoryChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>借阅趋势（近7天）</span>
            </div>
          </template>
          <div ref="borrowTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>热门图书 TOP 10</span>
            </div>
          </template>
          <div ref="topBooksChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>月度借阅统计</span>
            </div>
          </template>
          <div ref="monthlyChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { Reading, Collection, CircleCheck, Document, Download } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const stats = reactive({
  totalBooks: 0,
  totalBorrowed: 0,
  totalAvailable: 0,
  totalBorrowRecords: 0
})

const categoryChart = ref(null)
const borrowTrendChart = ref(null)
const topBooksChart = ref(null)
const monthlyChart = ref(null)

const getAuthHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

const fetchDashboardStats = async () => {
  try {
    const response = await axios.get('/api/statistics/dashboard', { headers: getAuthHeaders() })
    if (response.data.code === 200) {
      Object.assign(stats, response.data.data)
    }
  } catch (error) {
    console.error('获取仪表盘统计失败:', error)
  }
}

const fetchCategoryDistribution = async () => {
  try {
    const response = await axios.get('/api/statistics/category-distribution', { headers: getAuthHeaders() })
    if (response.data.code === 200) {
      const data = response.data.data
      const chart = echarts.init(categoryChart.value)
      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [{
          name: '图书分类',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: data.map(item => ({ name: item.name, value: item.value })),
          color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#C0ADDD', '#8E66AD', '#33CCCC']
        }]
      })
    }
  } catch (error) {
    console.error('获取分类分布失败:', error)
  }
}

const fetchBorrowTrend = async () => {
  try {
    const response = await axios.get('/api/statistics/borrow-trend', { headers: getAuthHeaders() })
    if (response.data.code === 200) {
      const data = response.data.data
      const chart = echarts.init(borrowTrendChart.value)
      chart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.date)
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [{
          name: '借阅数量',
          type: 'line',
          smooth: true,
          data: data.map(item => item.count),
          areaStyle: {
            color: 'rgba(64, 158, 255, 0.2)'
          },
          lineStyle: {
            color: '#409EFF'
          },
          itemStyle: {
            color: '#409EFF'
          }
        }]
      })
    }
  } catch (error) {
    console.error('获取借阅趋势失败:', error)
  }
}

const fetchTopBooks = async () => {
  try {
    const response = await axios.get('/api/statistics/top-books', { headers: getAuthHeaders() })
    if (response.data.code === 200) {
      const data = response.data.data
      const chart = echarts.init(topBooksChart.value)
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '9%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          minInterval: 1
        },
        yAxis: {
          type: 'category',
          data: data.map(item => item.title).reverse(),
          axisLabel: {
            interval: 0,
            rotate: 0
          }
        },
        series: [{
          name: '借阅次数',
          type: 'bar',
          data: data.map(item => item.borrowCount).reverse(),
          itemStyle: {
            color: '#67C23A'
          },
          label: {
            show: true,
            position: 'right'
          }
        }]
      })
    }
  } catch (error) {
    console.error('获取热门图书失败:', error)
  }
}

const fetchMonthlyStatistics = async () => {
  try {
    const response = await axios.get('/api/statistics/monthly', { headers: getAuthHeaders() })
    if (response.data.code === 200) {
      const data = response.data.data
      const chart = echarts.init(monthlyChart.value)
      chart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['借阅', '归还'],
          bottom: '10%',
          left: 'center'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '20%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.month)
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [{
          name: '借阅',
          type: 'bar',
          data: data.map(item => item.borrowCount),
          itemStyle: { color: '#409EFF' }
        }, {
          name: '归还',
          type: 'bar',
          data: data.map(item => item.returnCount),
          itemStyle: { color: '#67C23A' }
        }]
      })
    }
  } catch (error) {
    console.error('获取月度统计失败:', error)
  }
}

const exportReport = () => {
  const token = localStorage.getItem('token')
  fetch('/api/statistics/export', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  .then(response => response.blob())
  .then(blob => {
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '运营指标报表.xlsx'
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
    ElMessage.success('报表导出成功')
  })
  .catch(error => {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  })
}

const initCharts = () => {
  fetchDashboardStats()
  fetchCategoryDistribution()
  fetchBorrowTrend()
  fetchTopBooks()
  fetchMonthlyStatistics()
}

onMounted(() => {
  initCharts()
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.export-btn {
  margin-bottom: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: #fff;
}

.books-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.borrowed-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.available-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.records-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 8px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.chart-container {
  width: 100%;
  height: 300px;
}
</style>
