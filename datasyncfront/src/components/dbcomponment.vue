<template>
    <div>
        <el-form :label-position="labelPosition" label-width="80px" :model="form">
            <el-form-item label="数据库url">
                <el-input v-model="form.url"></el-input>
            </el-form-item>
            <el-form-item label="用户名">
                <el-input v-model="form.username"></el-input>
            </el-form-item>
            <el-form-item label="密码">
                <el-input v-model="form.password"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary">确认</el-button>
                <el-button>取消</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
  export default {
    name: 'dbcomponment', 
    data() {
      return {
        labelPosition: 'right',
        form: {
          url: '',
          username: '',
          password: ''
        }
      };
    }
  }
</script>