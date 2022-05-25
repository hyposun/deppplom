<template>
    <div>
        <b-form @submit="onSubmit">
            <b-form-group id="input-group-1" label="ФИО:" label-for="input-1">
                <b-form-input
                id="input-1"
                v-model="newUser.name"
                placeholder="ФИО"
                required
                ></b-form-input>
            </b-form-group>
            <b-form-group id="input-group-2" label="Логин:" label-for="input-2">
                <b-form-input
                id="input-2"
                v-model="newUser.login"
                placeholder="Логин"
                required
                ></b-form-input>
            </b-form-group>
            <b-form-group id="input-group-3" label="Пароль:" label-for="input-3">
                <b-form-input
                id="input-3"
                v-model="newUser.password"
                placeholder="Пароль"
                type="password"
                required
                ></b-form-input>
            </b-form-group>

            <b-form-group id="input-group-4" label="Выберите роль:" label-for="input-4">
                <b-form-select
                id="input-4"
                v-model="newUser.role"
                placeholder="Роль"
                :options="roles"
                required
                ></b-form-select>
            </b-form-group>

            <b-button type="submit" variant="primary">Сохранить</b-button>
        </b-form>
    </div>
</template>

<script>
  module.exports = {
    props: {
        user: {
            type: Object,
            required: false,
            default: {
                id: 0,
                name: "",
                login: "",
                password: "",
                groups: [],
                role: 'STUDENT'
            }
        }
    },
    data() {
      return {
        newUser: this.user,
        roles: [
            'ADMIN', 'TEACHER', 'STUDENT'
        ]
      };
    },
    methods: {
        onSubmit(event) {
            // todo smooth
            this.updateUser();
        },
        async updateUser() {

            const response = await fetch('/api/v0/users/update', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(this.newUser)
            }); 

            const data = await response.json();
            if (!response.ok) {
                const error = (data && data.message) || response.statusText;
                alert("Ошибка загрузки пользователей: " + error);
                return Promise.reject(error);
            }
        },
    },
  };
</script>