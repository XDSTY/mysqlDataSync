<template>
  <div>
      <el-form ref="form" :inline="true" :model="form" label-width="110px">
        <el-form-item>
          <el-button type="primary" size="small" @click="addOriginDB">源数据库</el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button type="primary" size="small" @click="addTargetDB">目标数据库</el-button>
        </el-form-item>
        <el-divider></el-divider>
        <el-form-item>
          <el-button type="primary" @click="submit">start</el-button>
        </el-form-item>
    </el-form>
    <div v-if="dialogVisible">
      <el-dialog :title="title" width="50%" :visible.sync="dialogVisible" :close-on-click-modal="false" append-to-body="">
        <dbcomponment></dbcomponment>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import dbcomponment from './dbcomponment'
import {datasync} from '../service/api'
  export default {
    data () {
      return {
        radio: '1',
        form: {
          fromDbUrl: '',
          fromDbUser: '',
          fromDbPassword: '',
          fromDbType: '1',
          toDbUrl: '',
          toDbUser: '',
          toDbPassword: '',
          toDbType: '1'
        },
        dialogVisible: false,
        dbType: 1, // 1为源数据库  2为目标数据库
        title: ''
      }
    },
    methods: {
      submit() {
        datasync.sync(this.form)
        .then(res=>{
          var data = res.data
          if(data.status === 0){
            this.$message({
              message: data.msg,
              type: 'success'
            });
          } else {
            this.$message.error(data.msg);
          }
        })
      },
      addOriginDB() {
        this.title = '源数据库'
        this.dialogVisible = true
      },
      addTargetDB() {
        this.title = '目标数据库'
        this.dialogVisible = true
      }
    },
    components: {
      dbcomponment
    }
  }
</script>