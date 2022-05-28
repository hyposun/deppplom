var HOST = ""
var api = {

    getMe: function () {
        return this.doGet("/api/v0/users/me")
    },

    doGet: async function (path) {

        let response = await fetch(`${HOST}${path}`)
        const data = await response.json();

        if (!response.ok) {
            const error = (data && data.message) || response.statusText;
            console.log("Ошибка при выполнении запроса: " + path, error)
            alert(error);

            return Promise.reject(error);
        }

        return Promise.resolve(data)
    },

    doPost: async function(path, body) {

        const response = await fetch(`${HOST}${path}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });
        const data = await response.json();
        if (!response.ok) {
            const error = (data && data.message) || response.statusText;
            console.log("Ошибка при выполнении запроса: " + path, error)
            alert(error);
            return Promise.reject(error);
        }

        return data
    }

}



new window.Vue({
  el: '#app',
  components: {
      'home-admin': window.httpVueLoader('components/admin/home.vue'),
      'home-teacher': window.httpVueLoader('components/teacher/home.vue'),
      'home-student': window.httpVueLoader('components/student/home.vue'),
  },
  computed: {
    getUser() {
      return api.getMe()
    },
    role() {
      return this.getUser().role
    }
  }
})