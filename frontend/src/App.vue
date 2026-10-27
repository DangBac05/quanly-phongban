
<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080/api/phong-ban'

const phongBans = ref([])
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const searchText = ref('')
const editingId = ref(null)

const form = ref({
  tenPhongBan: '',
  moTa: '',
  trangThai: true,
})

const filteredPhongBans = computed(() => {
  const keyword = searchText.value.trim().toLowerCase()

  if (!keyword) return phongBans.value

  return phongBans.value.filter((pb) =>
      `${pb.tenPhongBan} ${pb.moTa || ''}`.toLowerCase().includes(keyword),
  )
})

const tongSoPhongBan = computed(() => phongBans.value.length)
const dangHoatDong = computed(
    () => phongBans.value.filter((pb) => pb.trangThai === true).length,
)
const ngungHoatDong = computed(
    () => phongBans.value.filter((pb) => pb.trangThai === false).length,
)

function resetForm() {
  editingId.value = null
  form.value = {
    tenPhongBan: '',
    moTa: '',
    trangThai: true,
  }
}

async function loadPhongBans() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await axios.get(API_URL)
    phongBans.value = response.data
  } catch (error) {
    errorMessage.value =
        'Không thể tải dữ liệu. Hãy kiểm tra backend Spring Boot và kết nối PostgreSQL.'
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function savePhongBan() {
  errorMessage.value = ''
  successMessage.value = ''

  if (!form.value.tenPhongBan.trim()) {
    errorMessage.value = 'Vui lòng nhập tên phòng ban.'
    return
  }

  saving.value = true

  try {
    const data = {
      tenPhongBan: form.value.tenPhongBan.trim(),
      moTa: form.value.moTa.trim(),
      trangThai: form.value.trangThai,
    }

    if (editingId.value !== null) {
      await axios.put(`${API_URL}/${editingId.value}`, data)
      successMessage.value = 'Cập nhật phòng ban thành công!'
    } else {
      await axios.post(API_URL, data)
      successMessage.value = 'Thêm phòng ban thành công!'
    }

    resetForm()
    await loadPhongBans()
  } catch (error) {
    errorMessage.value = 'Lưu phòng ban thất bại. Vui lòng thử lại.'
    console.error(error)
  } finally {
    saving.value = false
  }
}

function editPhongBan(pb) {
  editingId.value = pb.id
  form.value = {
    tenPhongBan: pb.tenPhongBan || '',
    moTa: pb.moTa || '',
    trangThai: pb.trangThai ?? true,
  }

  errorMessage.value = ''
  successMessage.value = ''

  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function deletePhongBan(pb) {
  const confirmed = window.confirm(
      `Bạn có chắc muốn xóa phòng ban "${pb.tenPhongBan}" không?`,
  )

  if (!confirmed) return

  errorMessage.value = ''
  successMessage.value = ''

  try {
    await axios.delete(`${API_URL}/${pb.id}`)

    if (editingId.value === pb.id) {
      resetForm()
    }

    successMessage.value = 'Xóa phòng ban thành công!'
    await loadPhongBans()
  } catch (error) {
    errorMessage.value = 'Xóa phòng ban thất bại. Vui lòng thử lại.'
    console.error(error)
  }
}

onMounted(loadPhongBans)
</script>

<template>
  <main class="page">
    <header class="topbar">
      <div class="brand">
        <div class="brand-icon">PB</div>
        <div>
          <h1>Quản lý phòng ban</h1>
          <p>Hệ thống quản lý thông tin phòng ban</p>
        </div>
      </div>

      <button class="btn btn-outline refresh-btn" @click="loadPhongBans">
        ↻ Làm mới dữ liệu
      </button>
    </header>

    <section class="welcome">
      <div>
        <p class="eyebrow">TRANG QUẢN TRỊ</p>
        <h2>Danh sách phòng ban</h2>
        <p class="welcome-text">
          Quản lý, thêm mới, chỉnh sửa và theo dõi trạng thái phòng ban.
        </p>
      </div>
      <div class="welcome-symbol">▦</div>
    </section>

    <section class="stats">
      <article class="stat-card">
        <div class="stat-icon purple">▤</div>
        <div>
          <p>Tổng phòng ban</p>
          <strong>{{ tongSoPhongBan }}</strong>
        </div>
      </article>

      <article class="stat-card">
        <div class="stat-icon green">✓</div>
        <div>
          <p>Đang hoạt động</p>
          <strong>{{ dangHoatDong }}</strong>
        </div>
      </article>

      <article class="stat-card">
        <div class="stat-icon orange">Ⅱ</div>
        <div>
          <p>Ngừng hoạt động</p>
          <strong>{{ ngungHoatDong }}</strong>
        </div>
      </article>
    </section>

    <div v-if="errorMessage" class="alert alert-error">
      <span>⚠</span>
      {{ errorMessage }}
      <button class="alert-close" @click="errorMessage = ''">×</button>
    </div>

    <div v-if="successMessage" class="alert alert-success">
      <span>✓</span>
      {{ successMessage }}
      <button class="alert-close" @click="successMessage = ''">×</button>
    </div>

    <section class="content-grid">
      <article class="panel form-panel">
        <div class="panel-heading">
          <div>
            <h3>{{ editingId !== null ? 'Cập nhật phòng ban' : 'Thêm phòng ban mới' }}</h3>
            <p>Nhập thông tin phòng ban bên dưới</p>
          </div>
          <span class="heading-icon">{{ editingId !== null ? '✎' : '+' }}</span>
        </div>

        <form @submit.prevent="savePhongBan">
          <div class="form-group">
            <label for="tenPhongBan">Tên phòng ban <span>*</span></label>
            <input
                id="tenPhongBan"
                v-model="form.tenPhongBan"
                type="text"
                maxlength="100"
                placeholder="Ví dụ: Phòng Công nghệ thông tin"
                required
            />
          </div>

          <div class="form-group">
            <label for="moTa">Mô tả</label>
            <textarea
                id="moTa"
                v-model="form.moTa"
                rows="4"
                placeholder="Nhập mô tả về phòng ban..."
            ></textarea>
          </div>

          <div class="form-group">
            <label for="trangThai">Trạng thái</label>
            <select id="trangThai" v-model="form.trangThai">
              <option :value="true">Đang hoạt động</option>
              <option :value="false">Ngừng hoạt động</option>
            </select>
          </div>

          <div class="form-actions">
            <button class="btn btn-primary" type="submit" :disabled="saving">
              {{ saving ? 'Đang lưu...' : editingId !== null ? 'Lưu thay đổi' : '+ Thêm phòng ban' }}
            </button>
            <button
                v-if="editingId !== null"
                class="btn btn-outline"
                type="button"
                @click="resetForm"
            >
              Hủy
            </button>
            <button
                v-else
                class="btn btn-light"
                type="button"
                @click="resetForm"
            >
              Nhập lại
            </button>
          </div>
        </form>
      </article>

      <article class="panel list-panel">
        <div class="panel-heading list-heading">
          <div>
            <h3>Danh sách phòng ban</h3>
            <p>Thông tin được lấy trực tiếp từ cơ sở dữ liệu</p>
          </div>
          <span class="count-badge">{{ filteredPhongBans.length }} bản ghi</span>
        </div>

        <div class="search-box">
          <span>⌕</span>
          <input
              v-model="searchText"
              type="search"
              placeholder="Tìm theo tên hoặc mô tả..."
          />
          <button v-if="searchText" @click="searchText = ''" aria-label="Xóa tìm kiếm">
            ×
          </button>
        </div>

        <div v-if="loading" class="empty-state">
          <div class="loader"></div>
          <p>Đang tải dữ liệu...</p>
        </div>

        <div v-else-if="filteredPhongBans.length === 0" class="empty-state">
          <div class="empty-icon">▤</div>
          <h4>{{ searchText ? 'Không tìm thấy kết quả' : 'Chưa có phòng ban' }}</h4>
          <p>
            {{
              searchText
                  ? 'Thử tìm kiếm bằng từ khóa khác.'
                  : 'Hãy sử dụng biểu mẫu để thêm phòng ban đầu tiên.'
            }}
          </p>
        </div>

        <div v-else class="table-wrap">
          <table>
            <thead>
            <tr>
              <th class="id-column">ID</th>
              <th>Thông tin phòng ban</th>
              <th>Trạng thái</th>
              <th class="action-column">Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="pb in filteredPhongBans" :key="pb.id">
              <td class="id-column">#{{ pb.id }}</td>
              <td>
                <div class="department-name">{{ pb.tenPhongBan }}</div>
                <div class="department-desc">{{ pb.moTa || 'Chưa có mô tả' }}</div>
              </td>
              <td>
                  <span
                      class="status"
                      :class="pb.trangThai ? 'status-active' : 'status-inactive'"
                  >
                    <span class="status-dot"></span>
                    {{ pb.trangThai ? 'Hoạt động' : 'Ngừng hoạt động' }}
                  </span>
              </td>
              <td>
                <div class="row-actions">
                  <button class="icon-btn edit-btn" title="Sửa" @click="editPhongBan(pb)">
                    ✎
                  </button>
                  <button class="icon-btn delete-btn" title="Xóa" @click="deletePhongBan(pb)">
                    ×
                  </button>
                </div>
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <footer class="table-footer">
          <span>Hiển thị {{ filteredPhongBans.length }} / {{ tongSoPhongBan }} phòng ban</span>
          <span class="api-indicator"><span></span> API kết nối</span>
        </footer>
      </article>
    </section>

    <footer class="page-footer">
      <span>QUẢN LÝ PHÒNG BAN</span>
      <span>Spring Boot · Vue.js · PostgreSQL</span>
    </footer>
  </main>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

:root {
  font-family: 'Be Vietnam Pro', Arial, sans-serif;
  color: #20243a;
  background: #f5f6fb;
  font-synthesis: none;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  font-optical-sizing: auto;
  font-weight: 400;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  min-width: 320px;
  min-height: 100vh;
}

button,
input,
textarea,
select {
  font: inherit;
}

button {
  cursor: pointer;
}

button:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 30px 36px;
}

.topbar,
.brand,
.stats,
.stat-card,
.panel-heading,
.form-actions,
.row-actions,
.table-footer,
.page-footer {
  display: flex;
  align-items: center;
}

.topbar,
.panel-heading,
.table-footer,
.page-footer {
  justify-content: space-between;
}

.brand {
  gap: 13px;
}

.brand-icon {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: linear-gradient(135deg, #6258e9, #8975ff);
  color: white;
  font-size: 16px;
  font-weight: 800;
  box-shadow: 0 7px 18px #6558e932;
}

.brand h1 {
  margin: 0;
  font-size: 19px;
  font-weight: 800;
}

.brand p,
.panel-heading p {
  margin: 5px 0 0;
  color: #85899d;
  font-size: 12px;
}

.btn {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  gap: 7px;
  min-height: 40px;
  padding: 10px 15px;
  border: 1px solid transparent;
  border-radius: 9px;
  font-size: 12px;
  font-weight: 700;
  transition: 0.2s ease;
}

.btn-primary {
  color: white;
  background: #6558e8;
  box-shadow: 0 5px 12px #6558e829;
}

.btn-primary:hover {
  background: #5145d1;
}

.btn-outline {
  background: white;
  color: #53566d;
  border-color: #e3e5ef;
}

.btn-outline:hover,
.btn-light:hover {
  border-color: #b9b5f4;
  color: #6558e8;
}

.btn-light {
  background: #f2f1ff;
  color: #6558e8;
}

.welcome {
  margin-top: 32px;
  padding: 26px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 16px;
  color: white;
  background: linear-gradient(115deg, #6258e9 0%, #7768f2 65%, #9181ff 100%);
  overflow: hidden;
  position: relative;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 10px;
  letter-spacing: 1.5px;
  font-weight: 700;
  opacity: 0.8;
}

.welcome h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
}

.welcome-text {
  margin: 9px 0 0;
  font-size: 12px;
  opacity: 0.85;
}

.welcome-symbol {
  font-size: 76px;
  font-weight: 800;
  opacity: 0.17;
  margin-right: 18px;
}

.stats {
  gap: 18px;
  margin: 22px 0;
}

.stat-card {
  flex: 1;
  min-width: 0;
  gap: 15px;
  padding: 20px;
  background: white;
  border: 1px solid #eeeff6;
  border-radius: 13px;
  box-shadow: 0 3px 12px #292d5510;
}

.stat-icon {
  flex: 0 0 43px;
  width: 43px;
  height: 43px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  font-size: 21px;
  font-weight: 700;
}

.purple {
  color: #6558e8;
  background: #efedff;
}

.green {
  color: #20a875;
  background: #e6f8f0;
}

.orange {
  color: #e49430;
  background: #fff3e1;
}

.stat-card p {
  margin: 0 0 7px;
  color: #85899d;
  font-size: 11px;
}

.stat-card strong {
  font-size: 25px;
  font-weight: 800;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(280px, 0.8fr) minmax(0, 1.7fr);
  gap: 20px;
  align-items: start;
}

.panel {
  min-width: 0;
  background: white;
  border: 1px solid #eeeff6;
  border-radius: 14px;
  box-shadow: 0 3px 12px #292d550a;
}

.form-panel {
  padding: 23px;
}

.list-panel {
  overflow: hidden;
}

.list-heading {
  padding: 23px 23px 17px;
}

.panel-heading h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 800;
}

.heading-icon {
  display: grid;
  place-items: center;
  width: 35px;
  height: 35px;
  border-radius: 10px;
  background: #efedff;
  color: #6558e8;
  font-size: 23px;
  font-weight: 600;
}

.form-group {
  margin-top: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 700;
}

.form-group label span {
  color: #e55369;
}

.form-group input,
.form-group textarea,
.form-group select {
  display: block;
  width: 100%;
  padding: 11px 12px;
  color: #30334a;
  background: #fcfcfe;
  border: 1px solid #e3e5ef;
  border-radius: 8px;
  outline: none;
  font-size: 12px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus,
.search-box:focus-within {
  border-color: #8a7ff1;
  box-shadow: 0 0 0 3px #7165ed18;
}

.form-group textarea {
  resize: vertical;
  min-height: 90px;
}

.form-group input::placeholder,
.form-group textarea::placeholder,
.search-box input::placeholder {
  color: #adb0bf;
}

.form-actions {
  flex-wrap: wrap;
  gap: 9px;
  margin-top: 24px;
}

.form-actions .btn-primary {
  flex: 1;
}

.count-badge {
  white-space: nowrap;
  padding: 7px 10px;
  border-radius: 7px;
  background: #efedff;
  color: #6558e8;
  font-size: 10px;
  font-weight: 700;
}

.search-box {
  margin: 0 23px 16px;
  padding: 0 12px;
  height: 41px;
  display: flex;
  align-items: center;
  gap: 9px;
  background: #fcfcfe;
  border: 1px solid #e3e5ef;
  border-radius: 8px;
}

.search-box > span {
  color: #999db0;
  font-size: 23px;
}

.search-box input {
  width: 100%;
  min-width: 0;
  border: 0;
  outline: 0;
  background: transparent;
  color: #30334a;
  font-size: 11px;
}

.search-box button {
  border: 0;
  background: transparent;
  color: #898da0;
  font-size: 20px;
}

.table-wrap {
  width: 100%;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

thead {
  background: #f8f8fc;
}

th {
  padding: 12px 13px;
  color: #85899d;
  font-size: 10px;
  font-weight: 700;
  white-space: nowrap;
}

td {
  padding: 14px 13px;
  border-top: 1px solid #f0f1f6;
  font-size: 11px;
  vertical-align: middle;
}

.id-column {
  padding-left: 23px;
  color: #9296a8;
  white-space: nowrap;
}

.action-column {
  padding-right: 23px;
}

.department-name {
  min-width: 120px;
  color: #30334a;
  font-weight: 700;
  line-height: 1.6;
}

.department-desc {
  max-width: 230px;
  margin-top: 3px;
  color: #9296a8;
  font-size: 10px;
  line-height: 1.6;
  overflow-wrap: anywhere;
}

.status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 8px;
  border-radius: 20px;
  white-space: nowrap;
  font-size: 9px;
  font-weight: 700;
}

.status-dot,
.api-indicator > span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-active {
  color: #168c60;
  background: #e7f8f0;
}

.status-active .status-dot {
  background: #20b47e;
}

.status-inactive {
  color: #b77924;
  background: #fff3df;
}

.status-inactive .status-dot {
  background: #e6a23c;
}

.row-actions {
  gap: 6px;
}

.icon-btn {
  display: grid;
  place-items: center;
  width: 29px;
  height: 29px;
  border-radius: 7px;
  border: 1px solid transparent;
  font-size: 17px;
}

.edit-btn {
  color: #6558e8;
  background: #efedff;
}

.edit-btn:hover {
  border-color: #c9c3ff;
}

.delete-btn {
  color: #df5266;
  background: #fff0f1;
}

.delete-btn:hover {
  border-color: #ffc8ce;
}

.table-footer {
  padding: 15px 23px;
  border-top: 1px solid #f0f1f6;
  color: #9296a8;
  font-size: 10px;
}

.api-indicator {
  display: flex;
  align-items: center;
  gap: 7px;
  color: #20a875;
  font-weight: 700;
}

.api-indicator > span {
  background: #20b47e;
}

.alert {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 15px 0;
  padding: 12px 14px;
  border-radius: 9px;
  font-size: 12px;
}

.alert-error {
  color: #b9364b;
  background: #fff0f1;
  border: 1px solid #ffd8dd;
}

.alert-success {
  color: #168c60;
  background: #e9f9f1;
  border: 1px solid #c7f0dd;
}

.alert-close {
  margin-left: auto;
  padding: 0 5px;
  border: 0;
  background: transparent;
  color: inherit;
  font-size: 19px;
}

.empty-state {
  min-height: 220px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 25px;
  text-align: center;
}

.empty-icon {
  display: grid;
  place-items: center;
  width: 52px;
  height: 52px;
  border-radius: 15px;
  color: #8a7ff1;
  background: #f0eeff;
  font-size: 28px;
}

.empty-state h4 {
  margin: 13px 0 6px;
  font-size: 13px;
}

.empty-state p {
  margin: 0;
  color: #9296a8;
  font-size: 11px;
  line-height: 1.7;
}

.loader {
  width: 25px;
  height: 25px;
  border: 3px solid #e7e5ff;
  border-top-color: #6558e8;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.page-footer {
  margin-top: 27px;
  padding: 17px 2px;
  color: #999caf;
  font-size: 10px;
}

.page-footer span:first-child {
  font-weight: 800;
  letter-spacing: 0.7px;
}

@media (max-width: 1000px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .form-panel {
    max-width: none;
  }
}

@media (max-width: 650px) {
  .page {
    padding: 18px 13px;
  }

  .brand h1 {
    font-size: 15px;
  }

  .brand p {
    font-size: 10px;
  }

  .refresh-btn {
    padding: 9px;
    font-size: 10px;
  }

  .welcome {
    margin-top: 22px;
    padding: 22px 19px;
  }

  .welcome h2 {
    font-size: 19px;
  }

  .welcome-text {
    max-width: 250px;
    line-height: 1.7;
  }

  .welcome-symbol {
    display: none;
  }

  .stats {
    gap: 9px;
  }

  .stat-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    padding: 12px 10px;
  }

  .stat-icon {
    width: 35px;
    height: 35px;
    flex-basis: 35px;
  }

  .stat-card p {
    font-size: 9px;
    line-height: 1.5;
  }

  .stat-card strong {
    font-size: 21px;
  }

  .form-panel {
    padding: 18px;
  }

  .list-heading {
    padding: 18px 15px;
    gap: 10px;
  }

  .search-box {
    margin: 0 15px 14px;
  }

  .table-footer {
    padding: 13px 15px;
  }

  .page-footer {
    gap: 12px;
    font-size: 9px;
  }
}
</style>
