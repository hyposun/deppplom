<template>
    <div class="mb-2 p-3">
        <h3>Пользователи</h3>

        <b-btn v-b-modal="`create-user`" class="mb-3" variant="success">Создать пользователя</b-btn>
        <b-modal class="ml-auto" :id="`create-user`" hide-footer>
            <user-editor :user="generateBlankUser()"></user-editor>
        </b-modal>
        
        <b-form-group id="name-filter-group" label="ФИО:" label-for="name-filter-group">
                <b-form-input v-model="nameFilter" placeholder="ФИО"></b-form-input>
        </b-form-group>
        
        <b-btn class="mb-3" @click="loadUsers()">Поиск</b-btn>

        <b-table striped hover :fields="fields" :items="items">
            <template v-slot:cell(actions)="{ item }">
                <span><b-btn v-b-modal="`user-modal-${item.id}`">Редактировать</b-btn></span>
                <b-modal :id="`user-modal-${item.id}`" hide-footer>
                    <user-editor :user="item"></user-editor>
                </b-modal>
            </template>
        </b-table>
    </div>
</template>
<script>
  module.exports = {
    data() {
      return {
          fields: [
              {
                  key: 'id',
                  label: 'ID'
              },
              {
                  key: 'name',
                  label: 'ФИО'
              },
              {
                  key: 'login',
                  label: 'Логин'
              },
              {
                  key: 'role',
                  label: 'Роль'
              },
              {
                  key: 'actions',
                  label: 'Действия'
              }
          ],
          users: [],
          nameFilter: null,
          page: 0,
          size: 25
      };
    },
    components: {
      'user-editor': window.httpVueLoader('components/admin/user_editor.vue'),
    },
    mounted() {
        this.loadUsers();
    },
    computed: {
        items() {
            return this.users;
        }
    },
    methods: {
        generateBlankUser() {
            return {
                id: 0,
                name: "",
                login: "",
                password: "",
                groups: [],
                role: 'STUDENT'
            }
        },
        async loadUsers() {
            if (this.nameFilter != null) {
                this.users = await this.loadByUsernameFilter();
            } else {
                this.users = await this.loadDefault();
            }
        },
        async loadByUsernameFilter() {
            let response = await fetch(`/api/v0/users/findAllByNameLike?name=${this.nameFilter}`);
            const data = await response.json();
            if (!response.ok) {
                const error = (data && data.message) || response.statusText;
                alert("Ошибка загрузки пользователей: " + error);
                return Promise.reject(error);
            }
            return data;
        },
        async loadDefault() {
            let response = await fetch(`/api/v0/users?page=${this.page}&size=${this.size}`);
            const data = await response.json();
            if (!response.ok) {
                const error = (data && data.message) || response.statusText;
                alert("Ошибка загрузки пользователей: " + error);
                return Promise.reject(error);
            }
            return data.content;
        }
    },
  };
</script>