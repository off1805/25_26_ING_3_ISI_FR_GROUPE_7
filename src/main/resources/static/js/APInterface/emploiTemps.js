// Infrastructure
import { EmploiTempsApi }          from '../emploiTemps/infrastructure/EmploiTempsApi.js';
// Application
import { CreateEmploiTempsUC }     from '../emploiTemps/application/CreateEmploiTempsUC.js';
import { DeleteEmploiTempsUC }     from '../emploiTemps/application/DeleteEmploiTempsUC.js';
import { UpdateEmploiTempsUC }     from '../emploiTemps/application/UpdateEmploiTempsUC.js';
import { RetrieveEmploiTempsUC }   from '../emploiTemps/application/RetrieveEmploiTempsUC.js';
import { AddSeanceToEmploiUC }     from '../emploiTemps/application/AddSeanceToEmploiUC.js';
import { RemoveSeanceFromEmploiUC } from '../emploiTemps/application/RemoveSeanceFromEmploiUC.js';
// UI
import { EmploiTempsController }   from '../emploiTemps/ui/EmploiTempsController.js';

const _api = new EmploiTempsApi();

document.addEventListener('DOMContentLoaded', () => {
    new EmploiTempsController(
        new CreateEmploiTempsUC(_api),
        new DeleteEmploiTempsUC(_api),
        new UpdateEmploiTempsUC(_api),
        new RetrieveEmploiTempsUC(_api),
        new AddSeanceToEmploiUC(_api),
        new RemoveSeanceFromEmploiUC(_api)
    );
});
