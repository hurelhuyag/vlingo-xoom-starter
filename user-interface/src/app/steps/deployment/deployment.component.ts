import { SettingsStepService } from './../../service/settings-step.service';
import { DeploymentSettings } from './../../model/deployment-settings';
import { Component, OnInit } from '@angular/core';
import { StepComponent } from '../step.component';
import { NavigationDirection } from 'src/app/model/navigation-direction';
import { StepCompletion } from 'src/app/model/step-completion';
import { Step } from 'src/app/model/step';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-deployment',
  templateUrl: './deployment.component.html',
  styleUrls: ['./deployment.component.css']
})
export class DeploymentComponent extends StepComponent {

  deploymentForm: FormGroup;
  isDeploymentTypeSelected = false;
  isKubernetesSelected = false;

  constructor(private formBuilder: FormBuilder, private settingsStepService: SettingsStepService) {
    super();
    settingsStepService.getSettings$.pipe(map(settings => {
      if (settings && settings.deployment){
        return settings.deployment;
      }
      return {} as DeploymentSettings;
    })).subscribe(deployment => {
      this.createForm(deployment);
    });
  }

  ngOnInit(): void {
  }

  createForm(deployment: DeploymentSettings) {
    this.deploymentForm = this.formBuilder.group({
      clusterNodes: [ 3, Validators.required],
      type: ['', Validators.required],
      dockerImage: [deployment.dockerImage],
      kubernetesImage: [deployment.kubernetesImage],
      kubernetesPOD: [deployment.kubernetesPod]
    });

    const dockerImage = this.deploymentForm.get('dockerImage');
    const kubernetesImage = this.deploymentForm.get('kubernetesImage');
    const kubernetesPOD = this.deploymentForm.get('kubernetesPOD');

    this.deploymentForm.get('type').valueChanges
      .subscribe(type => {
        if (type === 'NONE') {
          dockerImage.setValidators(null);
          kubernetesImage.setValidators(null);
          kubernetesPOD.setValidators(null);
          this.isDeploymentTypeSelected = false;
          this.isKubernetesSelected = false;
        }

        if (type === 'DOCKER') {
          dockerImage.setValidators([Validators.required]);
          kubernetesImage.setValidators(null);
          kubernetesPOD.setValidators(null);
          this.isDeploymentTypeSelected = true;
          this.isKubernetesSelected = false;
        }

        if (type === 'KUBERNETES') {
          dockerImage.setValidators([Validators.required]);
          kubernetesImage.setValidators([Validators.required]);
          kubernetesPOD.setValidators([Validators.required]);
          this.isDeploymentTypeSelected = false;
          this.isKubernetesSelected = true;
        }

        dockerImage.updateValueAndValidity();
        kubernetesPOD.updateValueAndValidity();
        kubernetesImage.updateValueAndValidity();
      });
    this.deploymentForm.get('type').setValue(deployment.type);
  }

  next() {
    const deployment = this.deploymentForm.value as DeploymentSettings;
    this.settingsStepService.addDeployment(deployment);
    this.move(NavigationDirection.FORWARD);
  }

  previous() {
    const deployment = this.deploymentForm.value as DeploymentSettings;
    this.settingsStepService.addDeployment(deployment);
    this.move(NavigationDirection.REWIND);
  }

  private move(navigationDirection: NavigationDirection) {
    this.stepCompletion.emit(new StepCompletion(
      Step.DEPLOYMENT,
      this.deploymentForm.valid,
      navigationDirection
    ));
  }

  hasNext(): Boolean {
    return true;
  }

  hasPrevious(): Boolean {
    return true;
  }

  canFinish(): Boolean {
    return false;
  }

}
